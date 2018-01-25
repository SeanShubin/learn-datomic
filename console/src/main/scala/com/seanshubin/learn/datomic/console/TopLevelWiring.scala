package com.seanshubin.learn.datomic.console

import java.nio.charset.{Charset, StandardCharsets}

import com.seanshubin.devon.domain.{DevonMarshaller, DevonMarshallerWiring}
import com.seanshubin.learn.datomic.contract.{FilesContract, FilesDelegate}
import com.seanshubin.learn.datomic.domain._

trait TopLevelWiring {
  def commandLineArguments: Seq[String]

  lazy val fileSystem: FilesContract = FilesDelegate
  lazy val devonMarshaller: DevonMarshaller = DevonMarshallerWiring.Default
  lazy val charset: Charset = StandardCharsets.UTF_8
  lazy val emitLine: String => Unit = println
  lazy val configurationValidator: ConfigurationValidator = new ConfigurationValidatorImpl(fileSystem, devonMarshaller, charset)
  lazy val notifications: Notifications = new LineEmittingNotifications(devonMarshaller, emitLine)
  lazy val createConfigurationRunner: Configuration => Runnable = (theConfiguration) => new AfterConfigurationWiring {
    override def configuration: Configuration = theConfiguration
  }.afterConfigurationRunner
  lazy val topLevelRunner: Runnable = new TopLevelRunnerImpl(
    commandLineArguments,
    configurationValidator,
    notifications,
    createConfigurationRunner)
}
