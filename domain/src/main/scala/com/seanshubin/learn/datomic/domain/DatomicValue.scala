package com.seanshubin.learn.datomic.domain

sealed trait DatomicValue

case class DatomicKeyword(keyword: clojure.lang.Keyword) extends DatomicValue {
  override def toString: String = s"${keyword.getNamespace} ${keyword.getName}"
}
