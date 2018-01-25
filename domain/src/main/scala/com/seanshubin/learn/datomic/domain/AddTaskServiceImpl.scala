package com.seanshubin.learn.datomic.domain

class AddTaskServiceImpl(todoDatabaseService: TaskDatabaseService) extends AddTaskService {
  override def addTodo(name: String): Long = ???

  override def clearDone(): Unit = ???

  override def setDone(id: Long, done: Boolean): Unit = ???

  override def list(): Seq[Todo] = ???
}
