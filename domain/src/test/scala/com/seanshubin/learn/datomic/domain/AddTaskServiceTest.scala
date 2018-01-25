package com.seanshubin.learn.datomic.domain

import org.scalatest.FunSuite

class AddTaskServiceTest extends FunSuite {
  test("first todo item starts at id 1 and done = false") {
    val todoService: AddTaskService = new FakeAddTaskService
    todoService.addTodo("Say hello")
    val list = todoService.list()
    assert(list.size === 1)
    val todo = list.head
    assert(todo.name === "Say hello")
    assert(todo.id === 1)
    assert(todo.done === false)
  }

  test("todo items increment") {
    val todoService: AddTaskService = new FakeAddTaskService
    todoService.addTodo("a")
    todoService.addTodo("b")
    todoService.addTodo("c")
    val list = todoService.list()
    val ids = list.map(_.id)
    assert(ids === Seq(1, 2, 3))
  }

  test("change done status") {
    val todoService: AddTaskService = new FakeAddTaskService
    val id = todoService.addTodo("Say hello")
    assert(todoService.list().head.done === false)
    todoService.setDone(id, done = true)
    assert(todoService.list().head.done === true)
    todoService.setDone(id, done = false)
    assert(todoService.list().head.done === false)
  }

  test("clear done") {
    val todoService: AddTaskService = new FakeAddTaskService
    val idA = todoService.addTodo("a")
    val idB = todoService.addTodo("b")
    val idC = todoService.addTodo("c")
    val idD = todoService.addTodo("d")
    todoService.setDone(idB, done = true)
    todoService.setDone(idD, done = true)
    todoService.clearDone()
    assert(todoService.list().size === 2)
    assert(todoService.list()(0) === Todo(idA, "a", done = false))
    assert(todoService.list()(1) === Todo(idC, "c", done = false))
  }

  test("do not reuse old ordinals") {
    val todoService: AddTaskService = new FakeAddTaskService
    val firstId = todoService.addTodo("a")
    todoService.setDone(1, done = true)
    todoService.clearDone()
    val secondId = todoService.addTodo("a")
    assert(todoService.list().size === 1)
    assert(firstId !== secondId)
  }
}
