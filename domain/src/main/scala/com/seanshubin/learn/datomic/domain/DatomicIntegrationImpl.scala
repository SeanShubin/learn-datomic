package com.seanshubin.learn.datomic.domain

import java.io.{Reader, StringReader}
import java.util

import datomic.{Connection, ListenableFuture, Peer, Util}

class DatomicIntegrationImpl(connection: Connection) extends DatomicIntegration {
  override def execute(ednText: String): ListenableFuture[util.Map[_, _]] = {
    val reader: Reader = new StringReader(ednText)
    val list: util.List[_] = Util.readAll(reader)
    connection.transact(list)
  }

  override def queryExists(ednText: String): Boolean = {
    val rows = query(ednText)
    val exists = rows.size() != 0
    exists
  }

  override def queryValue[T](ednText: String, rowToValue: util.List[AnyRef] => T): T = {
    val rows = query(ednText)
    RowsUtil.rowsToValue(rows, rowToValue)
  }

  override def querySeq[T](ednText: String, rowToValue: util.List[AnyRef] => T): Seq[T] = {
    val rows = query(ednText)
    RowsUtil.rowsToSeq(rows, rowToValue)
  }

  private def query(ednText: String): util.Collection[util.List[AnyRef]] = {
    try {
      Peer.q(ednText, connection.db)
    } catch {
      case ex: Exception =>
        val message = s"${ex.getMessage}\n$ednText"
        throw new RuntimeException(message, ex)
    }
  }
}
