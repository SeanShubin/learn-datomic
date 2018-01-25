package com.seanshubin.learn.datomic.domain

import java.io.{Reader, StringReader}
import java.util

import clojure.lang.Keyword
import datomic.{Connection, Database, Peer, Util}

import scala.collection.JavaConverters._

object DatomicUtil {
  def withConnection(datomicUri: String)(f: Connection => Unit): Unit = {
    Peer.createDatabase(datomicUri)
    val connection = Peer.connect(datomicUri)
    try {
      f(connection)
    } finally {
      connection.release()
    }
  }

  def query[T](datalog: String, db: Database, rowConverter: util.List[AnyRef] => T): Iterable[T] = {
    val reader = new StringReader(datalog)
    val query = Util.readAll(reader)
    if (query.size() != 1) throw new RuntimeException(s"DatomicUtil.query expected exactly 1 element, got ${query.size}")
    val javaRows = Peer.q(query.get(0), db)
    val rows = javaRows.asScala.map(rowConverter)
    rows
  }

  def queryOne[T](datalog: String, db: Database, rowConverter: util.List[AnyRef] => T): T = {
    val rows = query(datalog, db, rowConverter)
    if (rows.size != 1) throw new RuntimeException(s"DatomicUtil.querySingle expected exactly 1 row, got ${rows.size}")
    rows.head
  }

  def createSchemaCommands(namespace: String, types: Map[String, String]): util.List[_] = {
    def toTypeSpecification(nameAndType: (String, String)): util.Map[_, _] = {
      val (name, theType) = nameAndType
      Util.map(
        ":db/id", Peer.tempid(":db.part/db"),
        ":db/ident", s":$namespace/$name",
        ":db/valueType", s":db.type/$theType",
        ":db/cardinality", ":db.cardinality/one",
        ":db.install/_attribute", ":db.part/db")
    }
    val elements: Iterable[util.Map[_, _]] = types.map(toTypeSpecification)
    val specification = Util.list(elements.toArray: _*)
    specification
  }

  def attributeTypesInNamespace(db: Database, namespace: String): Map[String, String] = {
    def inNamespace(attributeAndType: (Keyword, Keyword)) = {
      val (attribute, _) = attributeAndType
      attribute.getNamespace == namespace
    }
    def toNames(attributeAndType: (Keyword, Keyword)): (String, String) = {
      val (attribute, theType) = attributeAndType
      (attribute.getName, theType.getName)
    }
    val datalog =
      """[
        |  :find ?identity ?typeIdentity :where
        |  [_          :db.install/attribute ?attribute   ]
        |  [?attribute :db/ident             ?identity    ]
        |  [?attribute :db/valueType         ?type        ]
        |  [?type      :db/ident             ?typeIdentity]
        |]""".stripMargin
    val map = DatomicUtil.query(datalog, db, RowConverter.rowToKeywordKeyword).filter(inNamespace).map(toNames).toMap
    map
  }

  def allDatoms(db: Database): Iterable[DatomWrapper] = {
    def datomsForAttribute(attributeAndType: (Keyword, Keyword)) = {
      val (attribute, theType) = attributeAndType
      val datalog =
        s"""[
            |:find ?entity ?value :where
            |[?entity $attribute ?value]
            |]""".stripMargin
      val rows = query(datalog, db, RowConverter.rowToLongObject)
      def toDatomicWrapper(row: (Long, AnyRef)) = {
        val (entity, valueAnyRef) = row
        val value = DatomicPrimitive.byKeyword(theType).wrap(valueAnyRef)
        DatomWrapper(entity, attribute, value)
      }
      rows.map(toDatomicWrapper)
    }
    val allAttributes = queryAllAttributes(db)
    val allDatoms = allAttributes.flatMap(datomsForAttribute)
    allDatoms
  }

  def queryAllAttributes(db: Database): Iterable[(Keyword, Keyword)] = {
    val datalog =
      """[
        |  :find ?identity ?typeIdentity :where
        |  [_          :db.install/attribute ?attribute   ]
        |  [?attribute :db/ident             ?identity    ]
        |  [?attribute :db/valueType         ?type        ]
        |  [?type      :db/ident             ?typeIdentity]
        |]
        | """.stripMargin
    val attributes = query(datalog, db, RowConverter.rowToKeywordKeyword)
    attributes
  }

  def transact(connection: Connection, datalog: String): util.Map[_, _] = {
    connection.transact(datalogToList(datalog)).get()
  }

  def datalogToList(datalog: String): util.List[_] = {
    val reader: Reader = new StringReader(datalog)
    Util.readAll(reader)
  }
}
