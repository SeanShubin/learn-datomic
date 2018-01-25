package com.seanshubin.learn.datomic.prototype

import datomic.Peer

object DevDatomic extends App {
  //bin/transactor config/samples/dev-transactor-template.properties
  val (duration, _) = Timer.time {
    val datomicUri = "datomic:free://localhost:4334/world"
    Peer.createDatabase(datomicUri)
  }
  println(DurationFormat.MillisecondsFormat.format(duration))
  //bin/console --port 5334 mem datomic:dev://localhost:4334/hello
  //http://localhost:5334/browse
}
