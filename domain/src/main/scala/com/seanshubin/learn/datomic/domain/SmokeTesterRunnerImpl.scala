package com.seanshubin.learn.datomic.domain

class SmokeTesterRunnerImpl(schemaUpdaterLogic: SchemaUpdaterLogic, todoService: AddTaskService) extends Runnable {
  override def run(): Unit = {
    schemaUpdaterLogic.findAndApplyAll()
    val fooId = todoService.addTodo("foo")
    val barId = todoService.addTodo("bar")
    todoService.setDone(fooId, done = true)
    todoService.setDone(barId, done = false)
    todoService.clearDone()
    val expected = Seq(Todo(2, "bar", done = false))
    val actual = todoService.list()
    if (expected != actual) throw new RuntimeException(s"Expected $expected, got $actual")
  }
}
