package com.seanshubin.learn.datomic.domain

trait Notifications {
  def effectiveConfiguration(configuration: Configuration)

  def configurationError(lines: Seq[String])

  def topLevelException(exception: Throwable)

  def schemaUpdateResult(singleUpdateResult: SingleUpdateResult)
}
