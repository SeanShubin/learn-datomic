package com.seanshubin.learn.datomic.contract

import java.io.InputStream
import java.net.URL
import java.util

trait ClassLoaderNotImplemented extends ClassLoaderContract {
  override def loadClass(name: String): Class[_] = ???

  override def clearAssertionStatus(): Unit = ???

  override def getSystemResources(name: String): util.Enumeration[URL] = ???

  override def getSystemClassLoader: ClassLoader = ???

  override def getSystemResource(name: String): URL = ???

  override def setClassAssertionStatus(className: String, enabled: Boolean): Unit = ???

  override def getParent: ClassLoader = ???

  override def getResourceAsStream(name: String): InputStream = ???

  override def getResources(name: String): util.Enumeration[URL] = ???

  override def getSystemResourceAsStream(name: String): InputStream = ???

  override def setPackageAssertionStatus(packageName: String, enabled: Boolean): Unit = ???

  override def setDefaultAssertionStatus(enabled: Boolean): Unit = ???

  override def getResource(name: String): URL = ???
}
