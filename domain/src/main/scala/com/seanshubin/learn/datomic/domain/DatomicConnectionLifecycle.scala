package com.seanshubin.learn.datomic.domain

import datomic.Connection

trait DatomicConnectionLifecycle {
  def withConnection[T](datomicUri: String)(f: Connection => T): T
}
