package com.seanshubin.learn.datomic.domain

class FakeAddTaskService extends AddTaskService {
  private var byId: Map[Long, Todo] = Map()
  private var ids: Seq[Long] = Seq()
  private var ordinal: Long = 0

  override def addTodo(name: String): Long = {
    ordinal += 1
    val todo = Todo(ordinal, name, done = false)
    ids = ids :+ ordinal
    byId = byId.updated(ordinal, todo)
    ordinal
  }

  override def list(): Seq[Todo] = ids.map(byId)

  override def setDone(id: Long, done: Boolean): Unit = {
    byId = byId.updated(id, byId(id).copy(done = done))
  }

  override def clearDone(): Unit = {
    ids = ids.filter(notDone)
  }

  private def notDone(id: Long): Boolean = !byId(id).done
}
