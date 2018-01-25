package com.seanshubin.learn.datomic.prototype

trait RunnerFactory {
  def createRunner(configuration: Configuration): Runner
}
