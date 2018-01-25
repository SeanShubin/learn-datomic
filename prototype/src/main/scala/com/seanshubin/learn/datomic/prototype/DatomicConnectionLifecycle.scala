package com.seanshubin.learn.datomic.prototype

import datomic.{Connection, Peer}

object DatomicConnectionLifecycle {
  type LifecycleFunction[T] = ((Connection => T) => T)

  def createInMemoryLifecycleFunction[T](name: String): LifecycleFunction[T] = {
    def withInMemoryLifecycle(f: Connection => T): T = {
      val datomicUri = s"datomic:mem://$name"
      Peer.createDatabase(datomicUri)
      val theConnection = Peer.connect(datomicUri)
      try {
        f(theConnection)
      } finally {
        theConnection.release()
      }
    }
    withInMemoryLifecycle
  }
}
