package com.seanshubin.template.scala.console.console

import datomic.{Connection, Peer, Util}

object ParentChildSet extends App {
  def transactElectionSchema(connection: Connection) {
    def createColumn(namespace: String, name: String, dataType: String, cardinality: String) = {
      Util.map(
        ":db/id", Peer.tempid(":db.part/db"),
        ":db/ident", s":$namespace/$name",
        ":db/valueType", s":db.type/$dataType",
        ":db/cardinality", ":db.cardinality/" + cardinality,
        ":db.install/_attribute", ":db.part/db")
    }
    val electionName = createColumn("election", "name", "string", "one")
    val candidateName = createColumn("election", "candidate", "string", "many")
    connection.transact(Util.list(
      electionName,
      candidateName)).get()
  }

  def transactElectionSampleData(connection: Connection) {
    def transactElection(connection: Connection, electionName: String, candidateNames: Seq[String]) {
      val electionId = Peer.tempid(":db.part/user")
      val election = Util.map(":db/id", electionId, ":election/name", electionName)
      def createCandidateDatom(candidateName: String) = {
        Util.map(
          ":db/id", electionId,
          ":election/candidate", candidateName)
      }
      val candidates = candidateNames.map(createCandidateDatom)
      val datoms = election +: candidates
      connection.transact(Util.list(datoms: _*)).get()
    }
    transactElection(connection, "Favorite Programming Language", Seq("Scala", "Clojure", "Haskell"))
    transactElection(connection, "Least Evil Political Party", Seq("Nefarious", "Debauched", "Spoiler"))
    transactElection(connection, "Ice Cream", Seq("Chocolate", "Vanilla", "Strawberry"))
  }

  val uri = "datomic:mem://sample"
  Peer.createDatabase(uri)
  val connection = Peer.connect(uri)
  val baseline = connection.db();
  transactElectionSchema(connection)
  transactElectionSampleData(connection)
  val db = connection.db()

  DatomicReporter.report(baseline, db).foreach(println)

  sys.exit(0)
}
