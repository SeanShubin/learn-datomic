package com.seanshubin.learn.datomic.prototype

import java.io.StringReader
import java.lang.{Iterable => JavaIterable, Long => JavaLong}
import java.time._
import java.util.{List => JavaList}

import clojure.lang.Keyword
import com.seanshubin.learn.datomic.compare.SetDifference
import datomic.function.{Function => DatomicFunction}
import datomic.{Peer, Util}
import org.scalatest.FunSuite

class DatomicTest extends FunSuite {
  val withConnection = DatomicConnectionLifecycle.createInMemoryLifecycleFunction[Unit]("prototype")

  datomicTest("schema") {
    datomic =>
      val schemaUpdate: Map[String, DatomicType] = Map(
        "name" -> DatomicType.String,
        "ordinal" -> DatomicType.Long,
        "done" -> DatomicType.Boolean
      )
      datomic.updateSchema(schemaUpdate)
      val actual: Map[String, DatomicType] = datomic.schema()
      assert(actual === schemaUpdate)
  }

  //  test("create") {
  //    withConnection { connection =>
  //      val schemaUpdate: Map[String, DatomicType] = Map(
  //        "name" -> DatomicType.String,
  //        "ordinal" -> DatomicType.Long,
  //        "done" -> DatomicType.Boolean
  //      )
  //      val namespace = "todo"
  //      val datomic: Datomic = new DatomicImpl(connection, namespace)
  //      datomic.updateSchema(schemaUpdate)
  //      val oldTransactionFunctions = datomic.transactionFunctionNames().toSet
  //      val transactionFunction = TransactionFunction(
  //        name = "add-todo",
  //        parameters = Seq("name"),
  //        code = """syntax error - (str "Hello, " name "!")"""
  //      )
  //      datomic.addTransactionFunction(transactionFunction)
  //      val newTransactionFunctions = datomic.transactionFunctionNames().toSet
  //      val addedTransactionFunctions = newTransactionFunctions -- oldTransactionFunctions
  //      addedTransactionFunctions.foreach(println)
  //      val expectedFunctionNames = Seq("add-todo")
  //      assert(addedTransactionFunctions === expectedFunctionNames)
  //      datomic.execute("add-todo", "get create test working")
  //      val query =
  //        """[:find ?ordinal ?name ?done :in $ :where
  //          |[?ordinal :todo/name/string ?name]
  //          |[?ordinal :todo/done/boolean ?done]]""".stripMargin
  //      val rows = datomic.query(query)
  //      assert(rows.size === 1)
  //      val row = rows(0)
  //      assert(row(0) === 1L)
  //      assert(row(1) === "get create test working")
  //      assert(row(2) === false)
  //    }
  //  }
  //
  //  test("transaction function") {
  //    withConnection { connection =>
  //      val namespace = "todo"
  //      val datomic: Datomic = new DatomicImpl(connection, namespace)
  //      val transactionFunction = TransactionFunction(
  //        name = "hello-world",
  //        parameters = Seq("name"),
  //        code = """(str "Hello, " name "!")"""
  //      )
  //      datomic.addTransactionFunction(transactionFunction)
  //      val result = datomic.invokeTransactionFunction("hello-world", "world")
  //      assert(result === "Hello, world!")
  //    }
  //  }
  //
  datomicTest("all attributes") {
    datomic =>
      val actual = datomic.queryAllAttributeNames()
//      actual.map(s => "\"" + s + "\",").foreach(println)
      val expected = Seq(
        ":db/code",
        ":db.sys/reId",
        ":db/doc",
        ":db/fn",
        ":db.install/function",
        ":db/excise",
        ":db/cardinality",
        ":db/txInstant",
        ":db.excise/attrs",
        ":db.alter/attribute",
        ":db/noHistory",
        ":db/isComponent",
        ":db/fulltext",
        ":fressian/tag",
        ":db/index",
        ":db/lang",
        ":db.excise/before",
        ":db.excise/beforeT",
        ":db.sys/partiallyIndexed",
        ":db.install/valueType",
        ":db.install/partition",
        ":db/valueType",
        ":db/unique",
        ":db/ident",
        ":db.install/attribute"
      )
      val difference = SetDifference.diff(actual, expected)
      assert(difference.isSame, difference.messageLines.mkString("\n"))
  }

  datomicTest("all values for attribute") {
    datomic =>
      val date = ZonedDateTime.of(
        LocalDate.of(1970, Month.JANUARY, 1),
        LocalTime.of(0, 0, 0),
        ZoneId.of("UTC")).toInstant
      val all = datomic.queryAll().sorted
      assert(all.size === 221)
      assert(all(0) === EntityAttributeLong(0, Keyword.find("db.install", "attribute"), 8))
      assert(all(63) === EntityAttributeString(10, Keyword.find("db", "doc"), "Attribute used to uniquely name an entity."))
      assert(all(220) === EntityAttributeDate(13194139533375L, Keyword.find("db", "txInstant"), date))
//      all.foreach(println)
  }

  datomicTest("add something with data structure") {
    datomic =>
      val datom = Util.list("db/add", Peer.tempid("db.part/user"), "db/doc", "hello world")
      datomic.connection.transact(Util.list(datom)).get()
  }

  datomicTest("add something with reader") {
    datomic =>
      val datom = Util.readAll(new StringReader("[db/add #db/id[db.part/user -1000000] db/doc \"hello world\"]"))
      datomic.connection.transact(datom).get()

  }

  datomicTest("execute transaction function") {
    datomic =>
      val text1 =
        """;;add an entity called :foo
          |{:db/id #db/id [:db.part/user]
          |  :db/ident :foo}
          |
          |;;add a transaction function called add-doc
          |{:db/id #db/id [:db.part/user]
          |  :db/ident :add-doc
          |  :db/fn #db/fn {:lang "java"
          |                 :params [db e doc]
          |                 :code "return list(list(\":db/add\", e, \":db/doc\", doc));"}}
          | """.stripMargin
      val text2 =
        """
          |[:add-doc :foo "this is foo's doc"]
          | """.stripMargin
      val datom1 = Util.readAll(new StringReader(text1))
      val datom2 = Util.readAll(new StringReader(text2))
      datomic.connection.transact(datom1).get()
      datomic.connection.transact(datom2).get()
  }

  datomicTest("execute database function") {
    datomic =>
      val text =
        """
          |{
          |  :db/id #db/id [:db.part/user]
          |  :db/ident :greeting
          |  :db/fn #db/fn {
          |    :lang "java"
          |    :params [name]
          |    :code "return \"Hello, \" + name + \"!\";"
          |  }
          |}
        """.stripMargin
      val datom = Util.readAll(new StringReader(text))
      datomic.connection.transact(datom).get()
      val greetingEntity = datomic.connection.db().entity("greeting")
      val greetingFunction = greetingEntity.get(":db/fn").asInstanceOf[DatomicFunction]
      val value: String = greetingFunction.invoke("world").asInstanceOf[String]
//      println(value)
  }

  datomicTest("test database function") {
    datomic =>
      val function = Peer.function(Util.map(
        "lang", "java",
        "params", Util.list("name"),
        "code", "return \"Hello, \" + name + \"!\";"
      )).asInstanceOf[DatomicFunction]
      val message = function.invoke("world")
      assert(message === "Hello, world!")
  }

  datomicTest("test transaction function") {
    datomic =>
      val function = Peer.function(Util.map(
        "lang", "java",
        "params", Util.list("db", "e", "doc"),
        "code", "return list(list(\":db/add\", e, \":db/doc\", doc));"
      )).asInstanceOf[DatomicFunction]
      val text1 =
        """;;add an entity called :foo
          |{:db/id #db/id [:db.part/user]
          |  :db/ident :foo}
          | """.stripMargin
      val datom1 = Util.readAll(new StringReader(text1))
      datomic.connection.transact(datom1).get()
      val results = function.invoke(datomic.connection.db(), ":foo", "this is 'foo's doc").asInstanceOf[JavaList[AnyRef]]
      assert(results.size() === 1)
      val result = results.get(0).asInstanceOf[JavaList[AnyRef]]
      assert(result.size() === 4)
      assert(result.get(0) === ":db/add")
      assert(result.get(1) === ":foo")
      assert(result.get(2) === ":db/doc")
      assert(result.get(3) === "this is 'foo's doc")
  }

  def datomicTest(name: String)(f: Datomic => Unit): Unit = {
    test(name) {
      DatomicConnectionLifecycle.createInMemoryLifecycleFunction[Unit]("prototype") {
        connection =>
          val namespace = "test"
          val datomic: Datomic = new DatomicImpl(connection, namespace)
          val before = datomic.queryAll()

          f(datomic)

          val after = datomic.queryAll()
          val newStuff = after.toSet -- before.toSet
          val removedStuff = before.toSet -- after.toSet
          val testCaption = s"test - $name"
          val newLines = linesFor("new stuff", newStuff)
          val removedLines = linesFor("removed stuff", removedStuff)
          val lines = testCaption +: (newLines ++ removedLines)
//          lines.foreach(println)
      }
    }
  }

  def linesFor(caption: String, stuff: Set[EntityAttributeValue]): Seq[String] = {
    val head = s"$caption (${stuff.size})"
    val tail = stuff.toList.sorted.map(_.toString).map(s => "  " + s)
    head :: tail
  }
}
