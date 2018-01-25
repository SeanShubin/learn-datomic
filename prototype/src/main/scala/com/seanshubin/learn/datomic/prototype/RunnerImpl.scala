package com.seanshubin.learn.datomic.prototype

class RunnerImpl(target: String, emitLine: String => Unit) extends Runner {
  override def run(): Unit = emitLine(s"Hello, $target!")
}
