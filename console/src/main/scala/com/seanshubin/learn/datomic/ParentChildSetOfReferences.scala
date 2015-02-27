package com.seanshubin.learn.datomic

import java.util.{Map => JavaMap}

import datomic.{Connection, Peer, Util}

object ParentChildSetOfReferences extends App {
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
    val electionCandidate = createColumn("election", "candidate", "ref", "many")
    val candidateName = createColumn("candidate", "name", "string", "one")
    connection.transact(Util.list(
      electionName,
      electionCandidate,
      candidateName)).get()
  }

  def transactElectionSampleData(connection: Connection) {
    def transactElection(connection: Connection, electionName: String, candidateNames: Seq[String]) {
      val electionId = Peer.tempid(":db.part/user")
      val election = Util.map(":db/id", electionId, ":election/name", electionName)
      def createCandidateDatom(candidateName: String): Seq[JavaMap[_, _]] = {
        val candidateId = Peer.tempid(":db.part/user")
        val candidateNameData = Util.map(
          ":db/id", candidateId,
          ":candidate/name", candidateName)
        val electionCandidate = Util.map(
          ":db/id", electionId,
          ":election/candidate", candidateId)
        Seq(candidateNameData, electionCandidate)
      }
      val candidates = candidateNames.flatMap(createCandidateDatom)
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
