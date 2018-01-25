package com.seanshubin.learn.datomic.domain

import datomic.{Connection, Peer}
import org.scalatest.FunSuite

class TaskDatabaseServiceTest extends FunSuite {
  test("ordinal starts at one") {
    withConnection { connection =>
      val service: TaskDatabaseService = new TaskDatabaseServiceImpl()
      service.initialize(connection)
      assert(service.readNextOrdinal(connection.db) === 1)
    }
  }

  test("increment ordinal") {
    withConnection { connection =>
      val service: TaskDatabaseService = new TaskDatabaseServiceImpl()
      service.initialize(connection)
      val dbAfterIncrement = service.incrementNextOrdinal(connection)
      assert(service.readNextOrdinal(dbAfterIncrement) === 2)
    }
  }

  def withConnection(f: Connection => Unit): Unit = {
    val datomicUri: String = "datomic:mem://todo-database-service-test"
    Peer.createDatabase(datomicUri)
    val connection = Peer.connect(datomicUri)
    try {
      f(connection)
    } finally {
      connection.release()
    }
  }
}
