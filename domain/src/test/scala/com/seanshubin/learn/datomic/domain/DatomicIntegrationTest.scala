package com.seanshubin.learn.datomic.domain

import org.scalatest.FunSuite

class DatomicIntegrationTest extends FunSuite {
  test("execute") {
    lifecycle.withConnection("datomic:mem://DatomicIntegrationTest") {
      connection =>
        val datomic = new DatomicIntegrationImpl(connection)
        assert(datomic.queryExists("[:find ?e :in $ :where [?e :db/ident :foo]]") === false)
        datomic.execute("{:db/id #db/id [:db.part/user] :db/ident :foo}")
        assert(datomic.queryExists("[:find ?e :in $ :where [?e :db/ident :foo]]") === true)
    }
  }

  val lifecycle = new DatomicConnectionLifecycleImpl
}
