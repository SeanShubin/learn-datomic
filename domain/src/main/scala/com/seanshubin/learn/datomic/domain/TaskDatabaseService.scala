package com.seanshubin.learn.datomic.domain

import datomic.{Connection, Database}

trait TaskDatabaseService {
  def initialize(connection: Connection): Database

  def readNextOrdinal(db: Database): Long

  def incrementNextOrdinal(connection: Connection): Database
}
