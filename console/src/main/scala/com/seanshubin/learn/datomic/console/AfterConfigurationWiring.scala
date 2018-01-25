package com.seanshubin.learn.datomic.console

import com.seanshubin.devon.domain.{DevonMarshaller, DevonMarshallerWiring}
import com.seanshubin.learn.datomic.domain._
import datomic.Connection

trait AfterConfigurationWiring {
  def configuration: Configuration

  lazy val emitLine: String => Unit = println
  lazy val devonMarshaller: DevonMarshaller = DevonMarshallerWiring.Default
  lazy val notifications: Notifications = new LineEmittingNotifications(devonMarshaller, emitLine)
  lazy val datomicConnectionLifecycle: DatomicConnectionLifecycle = new DatomicConnectionLifecycleImpl
  lazy val createConnectionRunner: Connection => Runnable = (theConnection) => new AfterConnectionWiring {
    override def connection: Connection = theConnection
  }.afterConnectionRunner
  lazy val createSmokeTester: Connection => Runnable = (theConnection) => new SmokeTesterWiring {
    override def connection: Connection = theConnection
  }.smokeTester
  lazy val afterConfigurationRunner: Runnable = new AfterConfigurationRunnerImpl(
    configuration,
    notifications,
    datomicConnectionLifecycle,
    configuration.datomicUri,
    createConnectionRunner,
    configuration.smokeTesterDatomicUri,
    createSmokeTester
  )
}
