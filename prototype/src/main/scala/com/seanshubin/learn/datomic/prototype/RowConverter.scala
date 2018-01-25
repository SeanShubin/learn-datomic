package com.seanshubin.learn.datomic.prototype

import java.util.{List => JavaList}

import clojure.lang.Keyword

object RowConverter {

  case class EntityAndValueAsString(entity: Long, value: String)

  def keywordAsString(row: JavaList[AnyRef]): String = {
    assertRowSize(row, 1)
    val keyword = row.get(0).asInstanceOf[Keyword]
    keyword.toString
  }

  def keyword(row: JavaList[AnyRef]): Keyword = {
    assertRowSize(row, 1)
    val keyword = row.get(0).asInstanceOf[Keyword]
    keyword
  }

  def entityAndValueAsString(row: JavaList[AnyRef]): EntityAndValueAsString = {
    assertRowSize(row, 2)
    val entity = row.get(0).asInstanceOf[Long]
    val value = row.get(1).toString
    EntityAndValueAsString(entity, value)
  }

  def entityValue(row: JavaList[AnyRef]): EntityValue = {
    assertRowSize(row, 2)
    val entity = row.get(0).asInstanceOf[Long]
    val value = row.get(1)
    EntityValue(entity, value)
  }

  private def assertRowSize(row: JavaList[AnyRef], expectedSize: Int): Unit = {
    if (row.size() != expectedSize) throw new RuntimeException(s"Expected row of exactly size $expectedSize, got ${row.size()}")
  }
}