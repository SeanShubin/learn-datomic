package com.seanshubin.learn.datomic.domain

import java.time._
import java.util
import java.util.Date

import clojure.lang.Keyword
import datomic.{Peer, Util}
import org.scalatest.FunSuite

class DatomicUtilTest extends FunSuite {
  private val datomicUrl: String = "datomic:mem://todo"
  test("schema") {
    DatomicUtil.withConnection(datomicUrl) { connection =>
      val schema = Map("name/string" -> "string", "done/boolean" -> "boolean", "ordinal/long" -> "long")
      val addSchemaCommands = DatomicUtil.createSchemaCommands("task", schema)
      connection.transact(addSchemaCommands).get()
      val attributeTypes = DatomicUtil.attributeTypesInNamespace(connection.db(), "task")
      assert(attributeTypes === schema)
    }
  }

  test("all datoms") {
    DatomicUtil.withConnection(datomicUrl) { connection =>
      val allDatoms = DatomicUtil.allDatoms(connection.db()).toSeq.sorted
      val date = Date.from(ZonedDateTime.of(
        LocalDate.of(1970, Month.JANUARY, 1),
        LocalTime.of(0, 0, 0),
        ZoneId.of("UTC")).toInstant)
      assert(allDatoms.size === 221)
      assert(allDatoms(0) === DatomWrapper(0L, Keyword.find("db.install", "attribute"), DatomicPrimitive.DatomicRef.wrap(8L)))
      assert(allDatoms(63) === DatomWrapper(10L, Keyword.find("db", "doc"), DatomicPrimitive.DatomicString.wrap("Attribute used to uniquely name an entity.")))
      assert(allDatoms(220) === DatomWrapper(13194139533375L, Keyword.find("db", "txInstant"), DatomicPrimitive.DatomicInstant.wrap(date)))
    }
  }

  test("data types") {
    DatomicUtil.withConnection(datomicUrl) { connection =>
      val sampleDate = Date.from(ZonedDateTime.of(
        LocalDate.of(2000, Month.JANUARY, 1),
        LocalTime.of(0, 0, 0),
        ZoneId.of("UTC")).toInstant)
      val sampleFunction = Peer.function(Util.map(
        "lang", "java",
        "params", Util.list("name"),
        "code", "return \"Hello, \" + name + \"!\";"
      ))
      val sampleValues: Map[DatomicPrimitive, AnyRef] = Map(
        DatomicPrimitive.DatomicRef -> 1001L.asInstanceOf[AnyRef],
        DatomicPrimitive.DatomicKeyword -> "sample/keyword",
        DatomicPrimitive.DatomicLong -> 1002L.asInstanceOf[AnyRef],
        DatomicPrimitive.DatomicString -> "sample string",
        DatomicPrimitive.DatomicBoolean -> false.asInstanceOf[AnyRef],
        DatomicPrimitive.DatomicInstant -> sampleDate,
        DatomicPrimitive.DatomicFunction -> sampleFunction,
        DatomicPrimitive.DatomicBytes -> Seq[Byte](1, 2, 3).toArray,
        DatomicPrimitive.DatomicUuid -> util.UUID.fromString("6d1185b0-0ee9-4fc8-ad2b-2ae7c550e31e"),
        DatomicPrimitive.DatomicDouble -> 1.23.asInstanceOf[AnyRef],
        DatomicPrimitive.DatomicFloat -> 2.34f.asInstanceOf[AnyRef],
        DatomicPrimitive.DatomicUri -> java.net.URI.create("/sample/uri"),
        DatomicPrimitive.DatomicBigint -> new java.math.BigInteger("345"),
        DatomicPrimitive.DatomicBigdec -> new java.math.BigDecimal("123.45")
      )
      def ednToMakeColumn(primitive: DatomicPrimitive): String = {
        primitive.datalogForSchema("sample", "value")
      }
      def commandToAddSample(primitive: DatomicPrimitive): util.List[_] = {
        val tempId = Peer.tempid("db.part/user")
        val sampleValue = sampleValues(primitive)
        Util.list("db/add", tempId, s"sample/value/${primitive.keyword.getName}", sampleValue)
      }
      val datalog = DatomicPrimitive.values.map(ednToMakeColumn).mkString
      val addSampleCommands = DatomicPrimitive.values.map(commandToAddSample)
      val combinedAddSampleCommands = Util.list(addSampleCommands: _*)
      DatomicUtil.transact(connection, datalog)
      val before = DatomicUtil.allDatoms(connection.db)
      connection.transact(combinedAddSampleCommands).get()
      val after = DatomicUtil.allDatoms(connection.db)
      val added = after.toSet -- before.toSet
      assert(added.size === 15)
    }
  }

  test("invoke transaction function") {
    DatomicUtil.withConnection(datomicUrl) { connection =>
      val addAnEntityCalledFoo: String =
        """{
          |  :db/id #db/id [:db.part/user]
          |  :db/ident :foo
          |}""".stripMargin
      val addATransactionFunctionCalledAddDoc: String =
        """{
          |  :db/id #db/id [:db.part/user]
          |  :db/ident :add-doc
          |  :db/fn #db/fn {
          |    :lang "java"
          |    :params [db e doc]
          |    :code "return list(list(\":db/add\", e, \":db/doc\", doc));"
          |  }
          |}""".stripMargin
      val callsAddDocInATransaction: String = """[:add-doc :foo "this is foo's doc"]"""

      DatomicUtil.transact(connection, addAnEntityCalledFoo)
      DatomicUtil.transact(connection, addATransactionFunctionCalledAddDoc)
      DatomicUtil.transact(connection, callsAddDocInATransaction)
      assert(connection.db().entity(":foo").get(":db/doc") === "this is foo's doc")
    }
  }
}
