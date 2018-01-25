package com.seanshubin.learn.datomic.domain

import java.util.{Collection => JavaCollection, List => JavaList}

import scala.collection.JavaConverters._

object DatomicToScalaConversions {
  def scalifyRows(rows: JavaCollection[JavaList[AnyRef]]): Seq[Seq[DatomicValue]] = {
    val scalaRows = rows.asScala.map(scalifyRow).toSeq
    scalaRows
  }

  def scalifyRow(row: JavaList[AnyRef]): Seq[DatomicValue] = {
    val scalaCells = row.asScala.map(scalifyCell)
    scalaCells
  }

  def scalifyCell(cell: AnyRef): DatomicValue = {
    cell match {
      case keyword: clojure.lang.Keyword => DatomicKeyword(keyword)
      case _ => throw new RuntimeException(s"devonifyCell does not support type ${cell.getClass.getName}")
    }
  }
}
