package com.seanshubin.learn.datomic.domain

trait AddTaskService {
  def addTodo(name: String): Long

  def list(): Seq[Todo]

  def setDone(id: Long, done: Boolean)

  def clearDone(): Unit
}
