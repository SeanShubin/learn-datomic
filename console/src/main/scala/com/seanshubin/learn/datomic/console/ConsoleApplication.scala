package com.seanshubin.learn.datomic.console

object ConsoleApplication extends App {
  new TopLevelWiring {
    override def commandLineArguments: Seq[String] = args
  }.topLevelRunner.run()
}
