package com.seanshubin.template.scala.console.console

import datomic.{Connection, Peer, Util}

object ParentChildReference extends App {
  def transactElectionSchema(connection: Connection) {
    def createColumn(namespace: String, name: String, dataType: String) = {
      Util.map(
        ":db/id", Peer.tempid(":db.part/db"),
        ":db/ident", s":$namespace/$name",
        ":db/valueType", s":db.type/$dataType",
        ":db/cardinality", ":db.cardinality/one",
        ":db.install/_attribute", ":db.part/db")
    }
    val electionName = createColumn("election", "name", "string")
    val candidateName = createColumn("candidate", "name", "string")
    val candidateElection = createColumn("candidate", "election", "ref")
    connection.transact(Util.list(
      electionName,
      candidateName,
      candidateElection)).get()
  }

  def transactElectionSampleData(connection: Connection) {
    def transactElection(connection: Connection, electionName: String, candidateNames: Seq[String]) {
      val electionId = Peer.tempid(":db.part/user")
      val election = Util.map(":db/id", electionId, ":election/name", electionName)
      def createCandidateDatom(candidateName: String) = {
        val candidateId = Peer.tempid(":db.part/user")
        Util.map(
          ":db/id", candidateId,
          ":candidate/name", candidateName,
          ":candidate/election", electionId)
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
  val baseline = connection.db()
  transactElectionSchema(connection)
  transactElectionSampleData(connection)
  val db = connection.db()

  DatomicReporter.report(baseline, db).foreach(println)

  sys.exit(0)
}
