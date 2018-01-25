package com.seanshubin.learn.datomic.prototype

import scala.collection.mutable.ArrayBuffer

sealed abstract case class DatomicType(name: String) {
  DatomicType.valuesBuffer += this
}

object DatomicType {
  private val valuesBuffer = new ArrayBuffer[DatomicType]
  lazy val values = valuesBuffer.toSeq
  val String = new DatomicType("string") {}
  val Long = new DatomicType("long") {}
  val Boolean = new DatomicType("boolean") {}

  def datomicNameToEntry(datomicName: String): (String, DatomicType) = {
    val lastSlash = datomicName.lastIndexOf('/')
    val name = datomicName.substring(0, lastSlash)
    val typeName = datomicName.substring(lastSlash + 1)
    val datomicType = fromName(typeName)
    (name, datomicType)
  }

  def fromName(name: String): DatomicType = {
    values.find(_.name == name) match {
      case Some(datomicType) => datomicType
      case None => throw new RuntimeException(s"'$name' did not match a currently supported datomic type")
    }
  }


}
