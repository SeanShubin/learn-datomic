package com.seanshubin.learn.datomic.domain

import com.seanshubin.learn.datomic.domain.SingleUpdateResult.{AlreadyApplied, CouldNotVerify, Success, ThrewException}
import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer

class SchemaUpdaterLogicTest extends FunSuite {

  test("if no schemas then no updates") {
    val helper = new Helper()
    val report = helper.logic.findAndApplyAll()
    assert(report.isSuccess)
    assert(report.appliedPreviously.isEmpty)
    assert(report.appliedJustNow.isEmpty)
    assert(report.unableToApply.isEmpty)
    assert(helper.notifications.invocations === Seq())
    /*
    no schemas found in /schema, so nothing to do
     */
  }

  test("successful update") {
    val helper = new Helper(UpdateWillSucceed("one"))
    val report = helper.logic.findAndApplyAll()
    assert(report.isSuccess)
    assert(report.appliedPreviously.isEmpty)
    assert(report.appliedJustNow.map(_.ruleName) === Seq("one"))
    assert(report.unableToApply.isEmpty)
    assert(helper.notifications.invocations === Seq(Success("one")))
    /*
    [one] applied successfully
     */
  }

  test("failed update") {
    val helper = new Helper(UpdateWillFail("one"))
    val report = helper.logic.findAndApplyAll()
    assert(!report.isSuccess)
    assert(report.appliedPreviously.isEmpty)
    assert(report.appliedJustNow.isEmpty)
    assert(report.unableToApply.map(_.ruleName) === Seq("one"))
    assert(helper.notifications.invocations === Seq(CouldNotVerify("one")))
    /*
    [one] schema /schema/one/apply.edn was successfully executed, but did not take effect according to /schema/one/exists.edn
     */
  }

  test("update throws exception") {
    val helper = new Helper(UpdateWillThrow("one", new RuntimeException("update threw exception")))
    val report = helper.logic.findAndApplyAll()
    assert(!report.isSuccess)
    assert(report.appliedPreviously.isEmpty)
    assert(report.appliedJustNow.isEmpty)
    assert(report.unableToApply.map(_.ruleName) === Seq("one"))
    assert(helper.notifications.invocations.size === 1)
    val ThrewException(ruleName, exception) = helper.notifications.invocations.head
    assert(ruleName === "one")
    assert(exception.message === "update threw exception")
    /*
    [one] exception while executing schema /schema/one/apply.edn
      <stack trace>
     */
  }

  test("already applied update") {
    val helper = new Helper(AlreadyUpdated("one"))
    val report = helper.logic.findAndApplyAll()
    assert(report.isSuccess)
    assert(report.appliedPreviously.map(_.ruleName) === Seq("one"))
    assert(report.appliedJustNow.isEmpty)
    assert(report.unableToApply.isEmpty)
    assert(helper.notifications.invocations === Seq(AlreadyApplied("one")))
    /*
    [one] previously applied
     */
  }

  /*
  [three] skipped because dependencies have not been applied [one two]

   */

  sealed trait Update {
    def name: String

    def transition(): Update
  }

  case class AlreadyUpdated(name: String) extends Update {
    override def transition(): Update = throw new RuntimeException("Should not apply twice")
  }

  case class UpdateWillSucceed(name: String) extends Update {
    override def transition(): Update = AlreadyUpdated(name)
  }

  case class UpdateWillFail(name: String) extends Update {
    override def transition(): Update = this
  }

  case class UpdateWillThrow(name: String, exception: Exception) extends Update {
    override def transition(): Update = throw exception
  }

  class Helper(updates: Update*) {
    val names: Seq[String] = updates.map(_.name)
    var updateByName: Map[String, Update] = updates.map(x => x.name -> x).toMap
    val notifications: NotificationsStub = new NotificationsStub
    val whatHappened = new ArrayBuffer[String]
    val service = new SchemaUpdaterService {
      override def findRuleNames(): Seq[String] = names

      override def alreadyApplied(name: String): Boolean = updateByName(name) match {
        case _: AlreadyUpdated => true
        case _ => false
      }

      override def apply(name: String): Unit = {
        updateByName = updateByName.updated(name, updateByName(name).transition())
      }

      override def dependencies(name: String): Seq[String] = Seq()
    }
    val logic = new SchemaUpdaterLogicImpl(service, notifications)
  }

}
