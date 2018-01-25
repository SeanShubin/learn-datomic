package com.seanshubin.learn.datomic.domain

import java.io.InputStreamReader

import clojure.lang.Keyword
import datomic.{Connection, Entity, Peer, Util}
import org.scalatest.FunSuite

class TodoSampleTest extends FunSuite {
  val javaCode =
    """import datomic.Entity;
      |import java.util.List;
      |
      |Entity entity = ((Database) db).entity(":task/next-ordinal");
      |Long v = (Long) entity.get(":constant/value/long") + 1;
      |List result = list("db/add", entity.get(":db/ident"), ":constant/value/long", v);
      |return list(result);
      | """.stripMargin

  val clojureCode =
    """[[:db/add
      |  :task/next-ordinal
      |  :constant/value/long
      |  (->
      |    (d/entity db :task/next-ordinal)
      |    :constant/value/long
      |    (+ 1)
      |  )
      |]]
      | """.stripMargin

  test("counter schema") {
    withConnection { connection =>
      val x: Entity = null
      val diff = captureDiff(connection)(transactCounterSchema(connection))
//      diff.foreach(println)
    }
  }

  test("initialize counter") {
    withConnection { connection =>
      transactCounterSchema(connection)
      val diff = captureDiff(connection) {
        transactInitializeCounter(connection)
      }
//      diff.foreach(println)
    }
  }

  test("read counter") {
    withConnection { connection =>
      transactCounterSchema(connection)
      transactInitializeCounter(connection)
      val counter = readCounter(connection)
      assert(counter === 1)
    }
  }

  test("increment counter function java") {
    withConnection { connection =>
      val diff = captureDiff(connection) {
        transactIncrementFunctionJava(connection)
      }
//      diff.foreach(println)
    }
  }

  test("invoke increment counter java") {
    withConnection { connection =>
      transactCounterSchema(connection)
      transactInitializeCounter(connection)
      transactIncrementFunctionJava(connection)
      val diff = captureDiff(connection) {
        DatomicUtil.transact(connection, "[ :increment-task-next-ordinal ]")
        val counter = readCounter(connection)
        assert(counter === 2)
      }
//      diff.foreach(println)
    }
  }

  test("invoke increment counter clojure") {
    withConnection { connection =>
      transactCounterSchema(connection)
      transactInitializeCounter(connection)
      transactIncrementFunctionClojure(connection)
      val diff = captureDiff(connection) {
        DatomicUtil.transact(connection, "[ :increment-task-next-ordinal ]")
        val counter = readCounter(connection)
        assert(counter === 2)
      }
//      diff.foreach(println)
    }
  }

  test("define function java") {
    withConnection { connection =>
      transactCounterSchema(connection)
      transactInitializeCounter(connection)
      transactIncrementFunctionJava(connection)
      val diff = captureDiff(connection) {
        DatomicUtil.transact(connection, "[ :increment-task-next-ordinal ]")
      }
//      diff.foreach(println)
    }
  }

  test("invoke function java") {
    withConnection { connection =>
      transactCounterSchema(connection)
      transactInitializeCounter(connection)
      transactIncrementFunctionJava(connection)
      val function = connection.db().entity(":increment-task-next-ordinal").get(":db/fn").asInstanceOf[datomic.function.Function]
      val resultRows = function.invoke(connection.db()).asInstanceOf[java.util.List[_]]
      assert(resultRows.size() === 1)
      val resultRow = resultRows.get(0).asInstanceOf[java.util.List[_]]
      assert(resultRow.size() === 4)
      assert(resultRow.get(0).asInstanceOf[String] === "db/add")
      assert(resultRow.get(1).asInstanceOf[Keyword].getNamespace === "task")
      assert(resultRow.get(1).asInstanceOf[Keyword].getName === "next-ordinal")
      assert(resultRow.get(2).asInstanceOf[String] === ":constant/value/long")
      assert(resultRow.get(3).asInstanceOf[Long] === 2)
    }
  }

  test("invoke function clojure") {
    withConnection { connection =>
      transactCounterSchema(connection)
      transactInitializeCounter(connection)
      transactIncrementFunctionClojure(connection)
      val function = connection.db().entity(":increment-task-next-ordinal").get(":db/fn").asInstanceOf[datomic.function.Function]
      val resultRows = function.invoke(connection.db()).asInstanceOf[java.util.List[_]]
      assert(resultRows.size() === 1)
      val resultRow = resultRows.get(0).asInstanceOf[java.util.List[_]]
      assert(resultRow.size() === 4)
      assert(resultRow.get(0).asInstanceOf[Keyword].toString === ":db/add")
      assert(resultRow.get(1).asInstanceOf[Keyword].toString === ":task/next-ordinal")
      assert(resultRow.get(2).asInstanceOf[Keyword].toString === ":constant/value/long")
      assert(resultRow.get(3).asInstanceOf[Long] === 2)
    }
  }

  test("initialize.edn") {
    withConnection { connection =>
      val diff = captureDiff(connection) {
        invokeResource(connection, "todo/ordinal/generic-long-value.edn")
        invokeResource(connection, "todo/ordinal/initialize-next-ordinal-to-one.edn")
        invokeResource(connection, "todo/ordinal/function-to-increment-next-ordinal.edn")
        invokeResource(connection, "todo/ordinal/invoke-increment-next-ordinal.edn")
      }
//      diff.foreach(println)
    }
  }

  def invokeResource(connection: Connection, name: String): Unit = {
    val classLoader = this.getClass.getClassLoader
    val inputStream = classLoader.getResourceAsStream(name)
    val reader = new InputStreamReader(inputStream)
    val edn = Util.readAll(reader)
    connection.transact(edn).get()
  }

  def transactIncrementFunctionJava(connection: Connection): Unit = {
    val functionSchema =
      """{
        |  :db/id #db/id [:db.part/user]
        |  :db/ident     :increment-task-next-ordinal
        |  :db/fn #db/fn {
        |    :lang "java"
        |    :params [db]
        |    :code ---code---
        |  }
        |}""".stripMargin.replace("---code---", StringUtil.doubleQuote(javaCode))
    DatomicUtil.transact(connection, functionSchema)
  }

  def transactIncrementFunctionClojure(connection: Connection): Unit = {
    val functionSchema =
      """{
        |  :db/id #db/id [:db.part/user]
        |  :db/ident     :increment-task-next-ordinal
        |  :db/fn #db/fn {
        |    :lang "clojure"
        |    :params [db]
        |    :code ---code---
        |  }
        |}""".stripMargin.replace("---code---", StringUtil.doubleQuote(clojureCode))
    DatomicUtil.transact(connection, functionSchema)
  }

  def transactCounterSchema(connection: Connection): Unit = {
    val counterSchema =
      """
        |{
        |  :db/id                 #db/id[:db.part/db]
        |  :db/ident              :constant/id/keyword
        |  :db/valueType          :db.type/keyword
        |  :db/cardinality        :db.cardinality/one
        |  :db/doc                "global values"
        |  :db.install/_attribute :db.part/db
        |}
        |{
        |  :db/id                 #db/id[:db.part/db]
        |  :db/ident              :constant/value/long
        |  :db/valueType          :db.type/long
        |  :db/cardinality        :db.cardinality/one
        |  :db/doc                "value for constant of type long"
        |  :db.install/_attribute :db.part/db
        |}
        | """.stripMargin
    DatomicUtil.transact(connection, counterSchema)
  }

  def transactInitializeCounter(connection: Connection): Unit = {
    val initializeCounter =
      """{
        |  :db/id               #db/id[:db.part/user -1]
        |  :db/ident            :task/next-ordinal
        |  :constant/value/long 1
        |}
        | """.stripMargin
    DatomicUtil.transact(connection, initializeCounter)
  }

  def transactIncrementCounter(connection: Connection): Unit = {
    val initializeCounter =
      """{
        |  :db/id               #db/id[:db.part/user -1]
        |  :constant/id/keyword :task/next-ordinal
        |  :constant/value/long 1
        |}""".stripMargin
    DatomicUtil.transact(connection, initializeCounter)
  }

  def readCounter(connection: Connection): Long = {
    connection.db.entity(":task/next-ordinal").get(":constant/value/long").asInstanceOf[Long]
  }

  def withConnection(f: Connection => Unit): Unit = {
    val datomicUri: String = "datomic:mem://todo"
    Peer.createDatabase(datomicUri)
    val connection = Peer.connect(datomicUri)
    try {
      f(connection)
    } finally {
      connection.release()
    }
  }

  def captureDiff(connection: Connection)(f: => Unit): Seq[DatomWrapper] = {
    val before = DatomicUtil.allDatoms(connection.db)
    f
    val after = DatomicUtil.allDatoms(connection.db)
    val added = after.toSet -- before.toSet
    added.toSeq.sorted
  }
}
