package com.seanshubin.learn.datomic.contract

import java.io.{Console, InputStream, PrintStream}
import java.nio.channels.Channel
import java.util
import java.util.Properties

trait SystemNotImplemented extends SystemContract {
  override def in: InputStream = ???

  override def setSecurityManager(s: SecurityManager): Unit = ???

  override def runFinalization(): Unit = ???

  override def loadLibrary(libname: String): Unit = ???

  override def setIn(in: InputStream): Unit = ???

  override def gc(): Unit = ???

  override def getProperty(key: String): String = ???

  override def getProperty(key: String, default: String): String = ???

  override def identityHashCode(x: AnyRef): Int = ???

  override def setProperty(key: String, value: String): String = ???

  override def setProperties(props: Properties): Unit = ???

  override def arraycopy(src: AnyRef, srcPos: Int, dest: AnyRef, destPos: Int, length: Int): Unit = ???

  override def getSecurityManager: SecurityManager = ???

  override def console: Console = ???

  override def nanoTime: Long = ???

  override def setOut(out: PrintStream): Unit = ???

  override def clearProperty(key: String): String = ???

  override def getenv(name: String): String = ???

  override def getenv: util.Map[String, String] = ???

  override def currentTimeMillis: Long = ???

  override def setErr(err: PrintStream): Unit = ???

  override def load(filename: String): Unit = ???

  override def mapLibraryName(libname: String): String = ???

  @deprecated(
    message =
      "This method is inherently unsafe.  It may result in" +
        "finalizers being called on live objects while other threads are" +
        "concurrently manipulating those objects, resulting in erratic" +
        "behavior or deadlock.",
    since = "JDK1.1")
  override def runFinalizersOnExit(value: Boolean): Unit = ???

  override def out: PrintStream = ???

  override def inheritedChannel: Channel = ???

  override def err: PrintStream = ???

  override def exit(status: Int): Unit = ???

  override def lineSeparator: String = ???

  override def getProperties: Properties = ???
}
