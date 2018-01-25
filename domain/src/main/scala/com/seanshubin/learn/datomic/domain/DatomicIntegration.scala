package com.seanshubin.learn.datomic.domain

import java.util

import datomic.ListenableFuture

trait DatomicIntegration {
  def execute(ednText: String): ListenableFuture[util.Map[_, _]]

  def queryExists(ednText: String): Boolean

  def queryValue[T](ednText: String, rowToValue: util.List[AnyRef] => T): T

  def querySeq[T](ednText: String, rowToValue: util.List[AnyRef] => T): Seq[T]

}
