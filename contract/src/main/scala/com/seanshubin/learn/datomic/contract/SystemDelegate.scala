package com.seanshubin.learn.datomic.contract

import java.io.{Console, InputStream, PrintStream}
import java.nio.channels.Channel
import java.util
import java.util.Properties

class SystemDelegate extends SystemContract {
  override def in: InputStream = System.in

  override def setSecurityManager(s: SecurityManager): Unit = System.setSecurityManager(s)

  override def runFinalization(): Unit = System.runFinalization()

  override def loadLibrary(libname: String): Unit = System.loadLibrary(libname)

  override def setIn(in: InputStream): Unit = System.setIn(in)

  override def gc(): Unit = System.gc()

  override def getProperty(key: String): String = System.getProperty(key)

  override def getProperty(key: String, default: String): String = System.getProperty(key, default)

  override def identityHashCode(x: AnyRef): Int = System.identityHashCode(x)

  override def setProperty(key: String, value: String): String = System.setProperty(key, value)

  override def setProperties(props: Properties): Unit = System.setProperties(props)

  override def arraycopy(src: AnyRef, srcPos: Int, dest: AnyRef, destPos: Int, length: Int): Unit = System.arraycopy(src, srcPos, dest, destPos, length)

  override def getSecurityManager: SecurityManager = System.getSecurityManager

  override def console: Console = System.console()

  override def nanoTime: Long = System.nanoTime()

  override def setOut(out: PrintStream): Unit = System.setOut(out)

  override def clearProperty(key: String): String = System.clearProperty(key)

  override def getenv(name: String): String = System.getenv(name)

  override def getenv: util.Map[String, String] = System.getenv()

  override def currentTimeMillis: Long = System.currentTimeMillis()

  override def setErr(err: PrintStream): Unit = System.setErr(err)

  override def load(filename: String): Unit = System.load(filename)

  override def mapLibraryName(libname: String): String = System.mapLibraryName(libname)

  @deprecated(
    message =
      "This method is inherently unsafe.  It may result in" +
        "finalizers being called on live objects while other threads are" +
        "concurrently manipulating those objects, resulting in erratic" +
        "behavior or deadlock.",
    since = "JDK1.1")
  override def runFinalizersOnExit(value: Boolean): Unit = System.runFinalizersOnExit(value)

  override def out: PrintStream = System.out

  override def inheritedChannel: Channel = System.inheritedChannel()

  override def err: PrintStream = System.err

  override def exit(status: Int): Unit = System.exit(status)

  override def lineSeparator: String = System.lineSeparator()

  override def getProperties: Properties = System.getProperties
}
