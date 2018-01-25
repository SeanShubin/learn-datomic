package com.seanshubin.learn.datomic.compare

case class DifferenceResult(isSame: Boolean, messageLines: Seq[String])
