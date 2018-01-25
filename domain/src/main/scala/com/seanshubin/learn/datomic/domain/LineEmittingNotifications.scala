package com.seanshubin.learn.datomic.domain

import java.io.{PrintWriter, StringWriter}

import com.seanshubin.devon.domain.DevonMarshaller

class LineEmittingNotifications(devonMarshaller: DevonMarshaller, emit: String => Unit) extends Notifications {
  override def topLevelException(exception: Throwable): Unit = {
    exceptionLines(exception).foreach(emit)
  }

  override def effectiveConfiguration(configuration: Configuration): Unit = {
    val devon = devonMarshaller.fromValue(configuration)
    val pretty = devonMarshaller.toPretty(devon)
    emit("Effective configuration:")
    pretty.foreach(emit)
  }

  override def configurationError(lines: Seq[String]): Unit = {
    lines.foreach(emit)
  }

  override def schemaUpdateResult(singleUpdateResult: SingleUpdateResult): Unit = {
    emit("schema update: " + singleUpdateResult.toString)
  }

  private def exceptionLines(ex: Throwable): Seq[String] = {
    val stringWriter = new StringWriter()
    val printWriter = new PrintWriter(stringWriter)
    ex.printStackTrace(printWriter)
    val s = stringWriter.toString
    val lines = s.split( """\r\n|\r|\n""").toSeq
    lines
  }
}
