package com.seanshubin.learn.datomic.domain

trait SchemaUpdaterService {
  def findRuleNames(): Seq[String]

  def alreadyApplied(name: String): Boolean

  def dependencies(name: String): Seq[String]

  def apply(name: String): Unit
}
