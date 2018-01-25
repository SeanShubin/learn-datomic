package com.seanshubin.learn.datomic.domain

import java.util

import clojure.lang.Keyword
import com.seanshubin.learn.datomic.domain.DatomicPrimitive.ValueWrapper

import scala.collection.mutable.ArrayBuffer

sealed abstract case class DatomicPrimitive(keyword: Keyword, theClass: Class[_], attributeId: Long) {
  DatomicPrimitive.valuesBuffer += this

  def compare(x: AnyRef, y: AnyRef): Int = ???

  def datalogForSchema(namespace: String, name: String): String = {
    s"""{
        |:db/id                 #db/id[:db.part/db]
        |:db/ident              :$namespace/$name/${keyword.getName}
        |:db/valueType          :db.type/${keyword.getName}
        |:db/cardinality        :db.cardinality/one
        |:db.install/_attribute :db.part/db
        |}
     """.stripMargin
  }

  def typeAndValueString(x: AnyRef): String = f"$keyword%-16s ${valueString(x)}%s"

  def valueString(x: AnyRef): String = s"$x"

  def wrap(x: AnyRef):ValueWrapper = {
    if (x.getClass.getName != theClass.getName) throw new RuntimeException(s"$keyword expected $theClass, got ${x.getClass.getName}")
    ValueWrapper(this, x)
  }

  def wrap(x: Long): ValueWrapper = wrap(java.lang.Long.valueOf(x))
}

object DatomicPrimitive {
  private val valuesBuffer = new ArrayBuffer[DatomicPrimitive]
  lazy val values = valuesBuffer.toSeq
  lazy val byKeyword = values.map(x => (x.keyword, x)).toMap
  lazy val byAttributeId = values.map(x => (x.attributeId, x)).toMap
  val DatomicRef = new DatomicPrimitive(Keyword.find("db.type", "ref"), classOf[java.lang.Long], 20) {
    override def compare(x: AnyRef, y: AnyRef): Int = {
      Ordering.Long.compare(x.asInstanceOf[Long], y.asInstanceOf[Long])
    }
  }
  val DatomicKeyword = new DatomicPrimitive(Keyword.find("db.type", "keyword"), classOf[Keyword], 21) {
    override def valueString(x: AnyRef): String = {
      val keyword: Keyword = x.asInstanceOf[Keyword]
      s"${keyword.getNamespace} ${keyword.getName}"
    }
  }
  val DatomicLong = new DatomicPrimitive(Keyword.find("db.type", "long"), classOf[java.lang.Long], 22) {}
  val DatomicString = new DatomicPrimitive(Keyword.find("db.type", "string"), classOf[java.lang.String], 23) {}
  val DatomicBoolean = new DatomicPrimitive(Keyword.find("db.type", "boolean"), classOf[java.lang.Boolean], 24) {}
  val DatomicInstant = new DatomicPrimitive(Keyword.find("db.type", "instant"), classOf[java.util.Date], 25) {
    override def valueString(x: AnyRef): String = {
      x.asInstanceOf[util.Date].toInstant.toString
    }
  }
  val DatomicFunction = new DatomicPrimitive(Keyword.find("db.type", "fn"), classOf[datomic.function.Function], 26) {}
  val DatomicBytes = new DatomicPrimitive(Keyword.find("db.type", "bytes"), classOf[Array[Byte]], 27) {
    override def valueString(x: AnyRef): String = {
      val bytes = x.asInstanceOf[Array[Byte]].toSeq
      bytes.take(10).mkString(s"Bytes[${bytes.size}](", ", ", "...")
    }
  }
  val DatomicUuid = new DatomicPrimitive(Keyword.find("db.type", "uuid"), classOf[util.UUID], 56) {}
  val DatomicDouble = new DatomicPrimitive(Keyword.find("db.type", "double"), classOf[java.lang.Double], 57) {}
  val DatomicFloat = new DatomicPrimitive(Keyword.find("db.type", "float"), classOf[java.lang.Float], 58) {}
  val DatomicUri = new DatomicPrimitive(Keyword.find("db.type", "uri"), classOf[java.net.URI], 59) {}
  val DatomicBigint = new DatomicPrimitive(Keyword.find("db.type", "bigint"), classOf[java.math.BigInteger], 60) {}
  val DatomicBigdec = new DatomicPrimitive(Keyword.find("db.type", "bigdec"), classOf[java.math.BigDecimal], 61) {}

  case class ValueWrapper(theType: DatomicPrimitive, value: AnyRef) {
    override def toString: String = s"${theType.typeAndValueString(value)}"
  }
}
