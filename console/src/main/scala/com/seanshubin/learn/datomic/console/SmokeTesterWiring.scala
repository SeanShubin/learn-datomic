package com.seanshubin.learn.datomic.console

import com.seanshubin.devon.domain.{DevonMarshaller, DevonMarshallerWiring}
import com.seanshubin.learn.datomic.domain._
import datomic.Connection

trait SmokeTesterWiring {
  def connection: Connection

  lazy val classLoader: ClassLoader = getClass.getClassLoader
  lazy val classLoaderIntegration: ClassLoaderIntegration = new ClassLoaderIntegrationImpl(classLoader)
  lazy val classPathPrefix: String = "schema/"
  lazy val datomicIntegration: DatomicIntegration = new DatomicIntegrationImpl(connection)
  lazy val schemaUpdaterService: SchemaUpdaterService = new SchemaUpdaterServiceImpl(
    classLoaderIntegration,
    classPathPrefix,
    datomicIntegration
  )
  lazy val emitLine: String => Unit = println
  lazy val devonMarshaller: DevonMarshaller = DevonMarshallerWiring.Default
  lazy val notifications: Notifications = new LineEmittingNotifications(devonMarshaller, emitLine)
  lazy val schemaUpdaterLogic: SchemaUpdaterLogic = new SchemaUpdaterLogicImpl(schemaUpdaterService, notifications)
  lazy val todoDatabaseService: TaskDatabaseService = new TaskDatabaseServiceImpl
  lazy val todoService: AddTaskService = new AddTaskServiceImpl(todoDatabaseService)
  lazy val smokeTester: Runnable = new SmokeTesterRunnerImpl(schemaUpdaterLogic, todoService)
}
