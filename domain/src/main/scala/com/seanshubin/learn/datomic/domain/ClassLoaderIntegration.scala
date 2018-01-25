package com.seanshubin.learn.datomic.domain

import java.io.InputStream

trait ClassLoaderIntegration {
  def getResourceAsStream(name: String): InputStream

  def getResourceAsMaybeStream(name: String): Option[InputStream]
}
