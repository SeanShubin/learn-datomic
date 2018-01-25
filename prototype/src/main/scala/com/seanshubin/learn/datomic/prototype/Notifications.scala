package com.seanshubin.learn.datomic.prototype

trait Notifications {
  def effectiveConfiguration(configuration: Configuration)

  def configurationError(lines: Seq[String])

  def topLevelException(exception: Throwable)
}
