package com.seanshubin.learn.datomic.domain

import java.io.InputStream

class ClassLoaderIntegrationImpl(classLoader: ClassLoader) extends ClassLoaderIntegration {
  override def getResourceAsStream(name: String): InputStream = {
    val result = classLoader.getResourceAsStream(name)
    if (result == null) throw new RuntimeException(s"Unable to find '$name' on classpath")
    result
  }

  override def getResourceAsMaybeStream(name: String): Option[InputStream] = {
    val result = Option(classLoader.getResourceAsStream(name))
    result
  }
}
