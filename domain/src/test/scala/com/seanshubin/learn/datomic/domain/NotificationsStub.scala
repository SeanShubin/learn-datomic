package com.seanshubin.learn.datomic.domain

import com.seanshubin.devon.domain.DevonMarshallerWiring

import scala.collection.mutable.ArrayBuffer

class NotificationsStub extends Notifications {
  val invocations = new ArrayBuffer[SingleUpdateResult]
  override def effectiveConfiguration(configuration: Configuration): Unit = ???

  override def configurationError(lines: Seq[String]): Unit = ???

  override def topLevelException(exception: Throwable): Unit = ???

  override def schemaUpdateResult(singleUpdateResult: SingleUpdateResult): Unit = {
    invocations.append(singleUpdateResult)
  }
}
