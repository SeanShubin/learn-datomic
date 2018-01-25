package com.seanshubin.learn.datomic.domain

class NotificationsNotImplemented extends Notifications {
  override def effectiveConfiguration(configuration: Configuration): Unit = ???

  override def configurationError(lines: Seq[String]): Unit = ???

  override def schemaUpdateResult(singleUpdateResult: SingleUpdateResult): Unit = ???

  override def topLevelException(exception: Throwable): Unit = ???
}
