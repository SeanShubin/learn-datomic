package com.seanshubin.learn.datomic.domain

import java.{lang, util}

import clojure.lang.{ISeq, Keyword}
import com.seanshubin.devon.domain._
import datomic.db.{Datum, Db}

import scala.annotation.tailrec
import scala.collection.JavaConverters._

object DatomicToDevonConversions {
  def devonifyRows(rows: util.Collection[util.List[AnyRef]]): DevonArray = {
    val scalaRows = rows.asScala
    val devonRows = scalaRows.map(devonifyRow).toSeq
    DevonArray(devonRows)
  }

  def devonifyRow(row: util.List[AnyRef]): DevonArray = {
    val scalaCells = row.asScala
    val devonCells = scalaCells.map(devonifyCell)
    DevonArray(devonCells)
  }

  def devonifyCell(cell: AnyRef): Devon = {
    cell match {
      case keyword: clojure.lang.Keyword => DevonString(keyword.getNamespace + " " + keyword.getName)
      case _ => throw new RuntimeException(s"devonifyCell does not support type ${cell.getClass.getName}")
    }
  }

  def devonifyMap(map: util.Map[_, _]): DevonMap = {
    val javaMap: util.Map[AnyRef, AnyRef] = map.asInstanceOf[util.Map[AnyRef, AnyRef]]
    val scalaMap = javaMap.asScala
    DevonMap(scalaMap.toSeq.map(devonifyEntry).toMap)
  }

  def devonifyEntry(entry: (_, _)): (Devon, Devon) = {
    val (key, value) = entry
    val devonKey = devonify(key)
    val devonValue = devonify(value)
    (devonKey, devonValue)
  }

  def devonify(value: Any): Devon = {
    value match {
      case null => DevonNull
      case s: String => DevonString(s)
      case db: Db => DevonString("<db>")
      case datum: Datum => {
        //        val entity:Int = datum.e().asInstanceOf[Integer]
        //        val attributeId:Long = datum.a().asInstanceOf[Long]
        //        val attribute = DatomicPrimitive.byAttributeId(attributeId)
        DevonString(s"${datum.e()} ${datum.a()} ${datum.v()}")
      }
      case i: lang.Iterable[_] =>
        DevonArray(i.asScala.map(devonify).toSeq)
      case m: util.Map[_, _] => devonifyMap(m)
      case keyword: Keyword => DevonString(keyword.toString)
      case x => DevonString(x.toString)
      //      case btset:BTSet => DevonArray(iseqToList(Nil, btset.seq()).map(devonify))
      //      case x:Integer => DevonString(x.toString)
      //      case x:Long => DevonString(x.toString)
      //      case x:Boolean => DevonString(x.toString)
      //      case x:RAMFile => DevonString(s"RAMFile(length = ${x.getLength}})")
      //      case x:clojure.lang.Fn => DevonString("clojure function")
      //      case x:datomic.functions.Fn => DevonString("datomic function")
      //      case x => throw new RuntimeException(s"Unsupported type ${x.getClass.getName}")
    }
  }

  @tailrec
  def iseqToList(soFar: List[AnyRef], iseq: ISeq): List[AnyRef] = {
    if (iseq == null) soFar.reverse
    else iseqToList(iseq.first() :: soFar, iseq.next())
  }
}
