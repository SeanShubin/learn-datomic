package com.seanshubin.learn.datomic.domain

import java.io.InputStreamReader

import clojure.lang.Keyword
import datomic.{Connection, Database, Util}

class TaskDatabaseServiceImpl extends TaskDatabaseService {
  override def initialize(connection: Connection): Database = {
    invokeResource(connection, "todo/ordinal/generic-long-value.edn")
    invokeResource(connection, "todo/ordinal/initialize-next-ordinal-to-one.edn")
    invokeResource(connection, "todo/ordinal/function-to-increment-next-ordinal.edn")
  }

  override def incrementNextOrdinal(connection: Connection): Database = {
    invokeResource(connection, "todo/ordinal/invoke-increment-next-ordinal.edn")
  }

  override def readNextOrdinal(db: Database): Long = {
    db.entity(":task/next-ordinal").get(":value/long").asInstanceOf[Long]
  }

  private def invokeResource(connection: Connection, name: String): Database = {
    val classLoader = this.getClass.getClassLoader
    val inputStream = classLoader.getResourceAsStream(name)
    val reader = new InputStreamReader(inputStream)
    val edn = Util.readAll(reader)
    val transactionResult = connection.transact(edn).get()
    val dbAfter = Keyword.find("db-after")
    val newDb = transactionResult.get(dbAfter).asInstanceOf[Database]
    newDb
  }
}
