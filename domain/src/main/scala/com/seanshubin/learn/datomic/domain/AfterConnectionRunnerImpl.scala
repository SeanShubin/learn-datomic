package com.seanshubin.learn.datomic.domain

class AfterConnectionRunnerImpl(schemaUpdaterLogic: SchemaUpdaterLogic) extends Runnable {
  override def run(): Unit = {
    schemaUpdaterLogic.findAndApplyAll()
  }
}
