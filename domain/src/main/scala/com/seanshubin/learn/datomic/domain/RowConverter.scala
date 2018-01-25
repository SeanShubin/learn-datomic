package com.seanshubin.learn.datomic.domain

import java.util

import clojure.lang.Keyword

object RowConverter {
  def rowToKeywordKeyword(row: util.List[AnyRef]): (Keyword, Keyword) = {
    if (row.size != 2) throw new RuntimeException(s"Expected exactly 2 cells, got ${row.size}")
    val a1 = row.get(0).asInstanceOf[Keyword]
    val a2 = row.get(1).asInstanceOf[Keyword]
    (a1, a2)
  }

  def rowToLongObject(row: util.List[AnyRef]): (Long, AnyRef) = {
    if (row.size != 2) throw new RuntimeException(s"Expected exactly 2 cells, got ${row.size}")
    val a1 = row.get(0).asInstanceOf[java.lang.Long].toLong
    val a2 = row.get(1)
    (a1, a2)
  }

  def rowToLongKeywordObject(row: util.List[AnyRef]): (Long, Keyword, AnyRef) = {
    if (row.size != 3) throw new RuntimeException(s"Expected exactly 3 cells, got ${row.size}")
    val a1 = row.get(0).asInstanceOf[java.lang.Long].toLong
    val a2 = row.get(1).asInstanceOf[Keyword]
    val a3 = row.get(2)
    (a1, a2, a3)
  }

  def rowToKeyword(row: util.List[AnyRef]): Keyword = {
    if (row.size != 1) throw new RuntimeException(s"Expected exactly 1 cell, got ${row.size}")
    val a1 = row.get(0).asInstanceOf[Keyword]
    a1
  }

  def rowToLong(row: util.List[AnyRef]): Long = {
    if (row.size != 1) throw new RuntimeException(s"Expected exactly 1 cell, got ${row.size}")
    val a1 = row.get(0).asInstanceOf[java.lang.Long]
    a1
  }
}
