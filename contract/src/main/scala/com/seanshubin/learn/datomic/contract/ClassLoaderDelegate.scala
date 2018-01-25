package com.seanshubin.learn.datomic.contract

import java.io.InputStream
import java.net.URL
import java.util

class ClassLoaderDelegate(delegate: ClassLoader) extends ClassLoaderContract {
  override def loadClass(name: String): Class[_] = delegate.loadClass(name)

  override def clearAssertionStatus(): Unit = delegate.clearAssertionStatus()

  override def getSystemResources(name: String): util.Enumeration[URL] = ClassLoader.getSystemResources(name)

  override def getSystemClassLoader: ClassLoader = ClassLoader.getSystemClassLoader

  override def getSystemResource(name: String): URL = ClassLoader.getSystemResource(name)

  override def setClassAssertionStatus(className: String, enabled: Boolean): Unit = delegate.setClassAssertionStatus(className, enabled)

  override def getParent: ClassLoader = delegate.getParent

  override def getResourceAsStream(name: String): InputStream = delegate.getResourceAsStream(name)

  override def getResources(name: String): util.Enumeration[URL] = delegate.getResources(name)

  override def getSystemResourceAsStream(name: String): InputStream = ClassLoader.getSystemResourceAsStream(name)

  override def setPackageAssertionStatus(packageName: String, enabled: Boolean): Unit = delegate.setPackageAssertionStatus(packageName, enabled)

  override def setDefaultAssertionStatus(enabled: Boolean): Unit = delegate.setDefaultAssertionStatus(enabled)

  override def getResource(name: String): URL = delegate.getResource(name)
}
