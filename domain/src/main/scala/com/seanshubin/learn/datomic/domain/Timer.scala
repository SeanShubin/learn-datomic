package com.seanshubin.learn.datomic.domain

object Timer {
  def time[T](f: => T): (Long, T) = {
    val before = System.currentTimeMillis()
    val result = f
    val after = System.currentTimeMillis()
    val duration = after - before
    (duration, result)
  }
}
