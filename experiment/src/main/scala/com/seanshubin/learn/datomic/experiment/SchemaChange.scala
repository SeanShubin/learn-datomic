package com.seanshubin.learn.datomic.experiment

import datomic.{Connection, Peer, Util}

object SchemaChange extends App {
  def createColumn(namespace: String, name: String, dataType: String, cardinality: String) = {
    Util.map(
      ":db/id", Peer.tempid(":db.part/db"),
      ":db/ident", s":$namespace/$name",
      ":db/valueType", s":db.type/$dataType",
      ":db/cardinality", ":db.cardinality/" + cardinality,
      ":db.install/_attribute", ":db.part/db")
  }

  def transactElectionSchema(connection: Connection) {
    val electionName = createColumn("election", "name", "string", "one")
    val electionCandidate = createColumn("election", "candidate", "string", "many")
    connection.transact(Util.list(
      electionName,
      electionCandidate)).get()
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

  def transactElectionSchema2(connection: Connection) {
    val electionCandidate = createColumn("election", "candidate", "long", "many")
    connection.transact(Util.list(
      electionCandidate)).get()
  }

  def transactElectionSampleData2(connection: Connection) {
    def transactElection(connection: Connection, electionName: String, candidateNames: Seq[Long]) {
      val electionId = Peer.tempid(":db.part/user")
      val election = Util.map(":db/id", electionId, ":election/name", electionName)
      def createCandidateDatom(candidateName: Long) = {
        Util.map(
          ":db/id", electionId,
          ":election/candidate", candidateName.asInstanceOf[AnyRef])
      }
      val candidates = candidateNames.map(createCandidateDatom)
      val datoms = election +: candidates
      connection.transact(Util.list(datoms: _*)).get()
    }
    transactElection(connection, "Favorite Programming Language", Seq(1L, 2L, 3L))
    transactElection(connection, "Least Evil Political Party", Seq(4L, 5L, 6L))
    transactElection(connection, "Ice Cream", Seq(7L, 8L, 9L))
  }

  val uri = "datomic:mem://sample"
  Peer.createDatabase(uri)
  val connection = Peer.connect(uri)
  val baseline = connection.db();
  transactElectionSchema(connection)
  transactElectionSampleData(connection)
  transactElectionSchema2(connection)
  transactElectionSampleData2(connection)
  val db = connection.db()

  DatomicReporter.report(baseline, db).foreach(println)

  sys.exit(0)
}
