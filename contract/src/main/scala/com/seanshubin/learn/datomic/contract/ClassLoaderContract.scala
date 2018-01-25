package com.seanshubin.learn.datomic.contract

import java.io.InputStream
import java.net.URL
import java.util

trait ClassLoaderContract {
  def loadClass(name: String): Class[_]

  def getResource(name: String): URL

  def getResources(name: String): util.Enumeration[URL]

  def getSystemResource(name: String): URL

  def getSystemResources(name: String): util.Enumeration[URL]

  def getResourceAsStream(name: String): InputStream

  def getSystemResourceAsStream(name: String): InputStream

  def getParent: ClassLoader

  def getSystemClassLoader: ClassLoader

  def setDefaultAssertionStatus(enabled: Boolean)

  def setPackageAssertionStatus(packageName: String, enabled: Boolean)

  def setClassAssertionStatus(className: String, enabled: Boolean)

  def clearAssertionStatus()
}
