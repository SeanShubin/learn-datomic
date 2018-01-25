package com.seanshubin.learn.datomic.prototype

import java.util.{ArrayList => JavaArrayList, Collection => JavaCollection, HashMap => JavaHashMap, List => JavaList, Map => JavaMap}

import clojure.lang.Keyword
import com.seanshubin.learn.datomic.prototype.DatomicUtil._
import com.seanshubin.learn.datomic.prototype.RowConverter.EntityAndValueAsString
import datomic.{Connection, Peer, Util}

import scala.collection.JavaConverters._

class DatomicImpl(theConnection: Connection, namespace: String) extends Datomic {
  override def connection: Connection = theConnection

  override def schema(): Map[String, DatomicType] = {
    val query = datomify(Map(
      ":find" -> Seq("?identity"),
      ":where" -> Seq(
        Seq("_", ":db.install/attribute", "?attribute"),
        Seq("?attribute", ":db/ident", "?identity"))))
    val rows = Peer.q(query, theConnection.db)
    def inNamespace(keyword: Keyword) = keyword.sym.getNamespace == namespace
    val names = rowsToSequence(rows).map(rowToKeyword).filter(inNamespace).map(_.sym.getName)
    val entries = names.map(DatomicType.datomicNameToEntry)
    entries.toMap
  }

  override def updateSchema(fields: Map[String, DatomicType]): Unit = {
    val attributeSpecifications: Seq[JavaMap[_, _]] = fields.map(entryToAttributeSpecification).toSeq
    val attributeSpecificationList = Util.list(attributeSpecifications: _*)
    theConnection.transact(attributeSpecificationList)
  }

  override def addTransactionFunction(transactionFunction: TransactionFunction): Unit = {
    val fmap = Util.map(
      "lang", "clojure",
      "params", datomify(transactionFunction.parameters),
      "code", transactionFunction.code
    )
    val future = theConnection.transact(Util.list(fmap))
    val start = System.currentTimeMillis()
    while (!future.isDone) {
      Thread.sleep(1)
    }
    val end = System.currentTimeMillis()
    val duration = end - start
    val durationString = DurationFormat.MillisecondsFormat.format(duration)
    println(durationString)
  }

  override def transactionFunctionNames(): Seq[String] = {
    val datalog =
      """[:find ?identity :where
        |[_ :db.install/attribute ?attribute]
        |[?attribute :db/ident ?identity]]
      """.stripMargin
    query(datalog).map(_.toString())
  }

  override def execute(transactionFunctionName: String, parameters: String*): Unit = ???

  override def query(datalog: String): Seq[Seq[AnyRef]] = {
    val rows = rowsToSeqOfSeq(queryLatestDb(datalog))
    rows
  }

  override def query[T](datalog: String, rowConverter: JavaList[AnyRef] => T): Seq[T] = {
    val javaCollection = queryLatestDb(datalog)
    val scalaIterable = javaCollection.asScala
    val converted = scalaIterable.map(rowConverter)
    converted.toSeq
  }

  override def invokeTransactionFunction(name: String, args: AnyRef*): AnyRef = {
    ???
  }

  override def queryAllAttributeNames(): Seq[String] = {
    val datalog =
      """[
        |  :find
        |  ?identity
        |  :where
        |  [_          :db.install/attribute ?attribute]
        |  [?attribute :db/ident             ?identity]
        |]""".stripMargin
    val rows = query(datalog, RowConverter.keywordAsString)
    rows
  }

  private def queryAllAttributes(): Seq[Keyword] = {
    val datalog =
      """[
        |  :find
        |  ?identity
        |  :where
        |  [_          :db.install/attribute ?attribute]
        |  [?attribute :db/ident             ?identity]
        |]""".stripMargin
    val rows = query(datalog, RowConverter.keyword)
    rows
  }

  override def queryAllValuesForAttribute(attributeName: String): Seq[EntityAndValueAsString] = {
    val datalog =
      s"""[
          |:find ?entity ?value
          |:where [?entity $attributeName ?value]
          |]""".stripMargin
    query(datalog, RowConverter.entityAndValueAsString)
  }

  private def queryAllEntityValueForAttribute(attributeName: String): Seq[EntityValue] = {
    val datalog =
      s"""[
          |:find ?entity ?value
          |:where [?entity $attributeName ?value]
          |]""".stripMargin
    query(datalog, RowConverter.entityValue)
  }

  override def queryAll(): Seq[EntityAttributeValue] = {
    for {
      attribute <- queryAllAttributes()
      entityValue <- queryAllEntityValueForAttribute(attribute.toString)
    } yield {
      EntityAttributeValue.create(entityValue.entity, attribute, entityValue.value)
    }
  }

  private def queryLatestDb(datalog: String): JavaCollection[JavaList[AnyRef]] = {
    try {
      Peer.q(datalog, theConnection.db)
    } catch {
      case ex: Throwable => throw new RuntimeException(s"datalog = '$datalog'", ex)
    }
  }

  def entryToAttributeSpecification(entry: (String, DatomicType)): JavaMap[_, _] = {
    val (name, datomicType) = entry
    val attributeSpecification: JavaMap[_, _] = Util.map(
      ":db/id", Peer.tempid(":db.part/db"),
      ":db/ident", s":$namespace/$name/${datomicType.name}",
      ":db/valueType", s":db.type/${datomicType.name}",
      ":db/cardinality", ":db.cardinality/one",
      ":db.install/_attribute", ":db.part/db")
    attributeSpecification
  }

  def rowsToSeqOfSeq(rows: JavaCollection[JavaList[AnyRef]]): Seq[Seq[AnyRef]] = rowsToSequence(rows).map(rowToSequence)

  def rowsToSequence(rows: JavaCollection[JavaList[AnyRef]]): Seq[JavaList[AnyRef]] = new JavaArrayList(rows).asScala

  def rowToSequence(row: JavaList[AnyRef]): Seq[AnyRef] = new JavaArrayList(row).asScala

  def rowToKeyword(row: JavaList[AnyRef]): Keyword = {
    if (row.size() != 1) throw new RuntimeException(s"expected row to be size 1, got ${row.size()}")
    val keyword = row.get(0).asInstanceOf[Keyword]
    keyword
  }
}
