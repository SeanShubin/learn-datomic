package com.seanshubin.learn.datomic.prototype

import java.util.{ArrayList => JavaArrayList, Collection => JavaCollection, HashMap => JavaHashMap, List => JavaList, Map => JavaMap}

import com.seanshubin.devon.domain._
import datomic.Util

import scala.collection.JavaConverters._

object DatomicUtil {
  def datomify(root: AnyRef): AnyRef = {
    root match {
      case x: String => Util.read(x)
      case x: Seq[_] => datomifySeq(x)
      case x: Map[_, _] => datomifyMap(x)
      case unsupported => throw new RuntimeException("unsupported type " + unsupported.getClass.getName)
    }
  }

  def scalify(root: Any): AnyRef = {
    root match {
      case null => null
      case javaCollection: java.util.Collection[_] => scalifyCollection(javaCollection)
      case keyword: clojure.lang.Keyword => keyword.toString
      case _ =>
        val typeName = root.getClass.getName
        throw new RuntimeException(s"Type '$typeName' is not supported")
    }
  }

  def datomicToDevon(root: Any): Devon = {
    root match {
      case null =>
        DevonNull
      case javaCollection: java.util.Collection[_] =>
        DevonArray(javaCollection.asScala.map(datomicToDevon).toSeq)
      case keyword: clojure.lang.Keyword => DevonString(keyword.toString)
      case _ =>
        throw new RuntimeException(s"Type '${root.getClass.getName}' is not supported")
    }
  }

  private def datomifyMap(keyValues: Map[_, _]): AnyRef = {
    val javaMap: JavaMap[AnyRef, AnyRef] = new JavaHashMap[AnyRef, AnyRef]
    for ((key, value) <- keyValues) {
      javaMap.put(datomify(key.asInstanceOf[AnyRef]), datomify(value.asInstanceOf[AnyRef]))
    }
    javaMap
  }

  private def datomifySeq(elements: Seq[_]): AnyRef = {
    val javaList: JavaList[AnyRef] = new JavaArrayList[AnyRef]
    def addToList(element: Any) {
      javaList.add(datomify(element.asInstanceOf[AnyRef]))
    }
    elements.foreach(addToList)
    javaList
  }

  private def scalifyCollection(javaCollection: java.util.Collection[_]): Iterable[AnyRef] = {
    javaCollection.asScala.map(scalify)
  }
}
