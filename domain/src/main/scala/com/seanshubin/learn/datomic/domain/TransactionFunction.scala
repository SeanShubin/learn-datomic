package com.seanshubin.learn.datomic.domain

case class TransactionFunction(name: String, parameters: Seq[String], code: String)
