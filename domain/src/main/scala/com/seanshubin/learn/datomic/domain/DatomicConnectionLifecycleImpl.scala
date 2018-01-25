package com.seanshubin.learn.datomic.domain

import datomic.{Connection, Peer}

class DatomicConnectionLifecycleImpl extends DatomicConnectionLifecycle {
  override def withConnection[T](datomicUri: String)(f: (Connection) => T): T = {
    Peer.createDatabase(datomicUri)
    val connection = Peer.connect(datomicUri)
    try {
      f(connection)
    } finally {
      connection.release()
    }
  }
}
