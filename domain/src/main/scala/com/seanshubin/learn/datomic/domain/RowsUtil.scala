package com.seanshubin.learn.datomic.domain

import java.util

import scala.collection.JavaConverters._

object RowsUtil {
  val RowToBoolean: util.List[AnyRef] => Boolean = (row) => cellToBoolean(rowToCell(row))

  def rowsToValue[T](rows: util.Collection[util.List[AnyRef]], rowToValue: util.List[AnyRef] => T): T = {
    val row = RowsUtil.rowsToRow(rows)
    val value = rowToValue(row)
    value
  }

  def rowsToSeq[T](rows: util.Collection[util.List[AnyRef]], rowToValue: util.List[AnyRef] => T): Seq[T] = {
    val seq = rows.asScala.map(rowToValue).toSeq
    seq
  }

  def rowsToBoolean(rows: util.Collection[util.List[AnyRef]]): Boolean = {
    rowToBoolean(rowsToRow(rows))
  }

  def rowsToRow(rows: util.Collection[util.List[AnyRef]]): util.List[AnyRef] = {
    if (rows.size != 1) throw new RuntimeException(s"Expected exactly 1 row, got ${rows.size}")
    rows.iterator().next()
  }

  def rowToBoolean(row: util.List[AnyRef]): Boolean = {
    cellToBoolean(rowToCell(row))
  }

  def rowToCell(row: util.List[AnyRef]): AnyRef = {
    if (row.size() != 1) throw new RuntimeException(s"Expected exactly 1 cell, got ${row.size}")
    row.get(0)
  }

  def cellToBoolean(cell: AnyRef): Boolean = {
    cell.asInstanceOf[Boolean]
  }
}
