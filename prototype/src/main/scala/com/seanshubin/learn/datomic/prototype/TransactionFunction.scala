package com.seanshubin.learn.datomic.prototype

case class TransactionFunction(name: String, parameters: Seq[String], code: String)
