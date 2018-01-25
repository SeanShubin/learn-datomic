package com.seanshubin.learn.datomic.domain

import datomic.Connection

class AfterConfigurationRunnerImpl(configuration: Configuration,
                                   notifications: Notifications,
                                   datomicConnectionLifecycle: DatomicConnectionLifecycle,
                                   datomicUri: String,
                                   createConnectionRunner: Connection => Runnable,
                                   smokeTestDatomicUri: String,
                                   createSmokeTester: Connection => Runnable) extends Runnable {
  override def run(): Unit = {
    notifications.effectiveConfiguration(configuration)
    datomicConnectionLifecycle.withConnection(smokeTestDatomicUri) {
      theConnection => createSmokeTester(theConnection).run()
    }
    datomicConnectionLifecycle.withConnection(datomicUri) {
      theConnection =>
        createConnectionRunner(theConnection).run()
    }
  }
}
