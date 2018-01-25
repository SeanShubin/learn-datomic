package com.seanshubin.learn.datomic.domain

import java.util

import datomic.ListenableFuture
import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer

class SchemaUpdaterServiceTest extends FunSuite {
  test("find rule names") {
    val helper = new Helper()
    val expected = Seq("alpha", "beta", "gamma")
    val actual = helper.schemaUpdater.findRuleNames()
    assert(actual === expected)
  }

  test("apply") {
    val helper = new Helper()
    helper.schemaUpdater.apply("alpha")
    assert(helper.actuallyExecuted === Seq("datomic schema alpha"))
  }

  test("dependencies") {
    val helper = new Helper()
    val actual = helper.schemaUpdater.dependencies("alpha")
    assert(actual === Seq("delta", "epsilon", "zeta"))
  }

  test("already applied returns true") {
    val helper = new Helper(alreadyAppliedResult = Some(true))
    val actual = helper.schemaUpdater.alreadyApplied("alpha")
    assert(helper.actuallyExecuted === Seq("datomic exists alpha"))
    assert(actual === true)
  }

  test("already applied returns false") {
    val helper = new Helper(alreadyAppliedResult = Some(false))
    val actual = helper.schemaUpdater.alreadyApplied("alpha")
    assert(helper.actuallyExecuted === Seq("datomic exists alpha"))
    assert(actual === false)
  }

  class Helper(val alreadyAppliedResult: Option[Boolean] = None) {
    val classLoader: ClassLoader = this.getClass.getClassLoader
    val classLoaderIntegration: ClassLoaderIntegration = new ClassLoaderIntegrationImpl(classLoader)
    val classPathPrefix = "schema-updater-service-test/"
    val actuallyExecuted = new ArrayBuffer[String]()
    val datomicIntegration: DatomicIntegration = new DatomicIntegration {

      override def execute(ednText: String): ListenableFuture[util.Map[_, _]] = {
        actuallyExecuted.append(ednText)
        null
      }

      override def queryExists(ednText: String): Boolean = {
        actuallyExecuted.append(ednText)
        alreadyAppliedResult.get
      }

      override def queryValue[T](ednText: String, rowToValue: (util.List[AnyRef]) => T): T = ???

      override def querySeq[T](ednText: String, rowToValue: (util.List[AnyRef]) => T): Seq[T] = ???
    }
    val schemaUpdater: SchemaUpdaterService = new SchemaUpdaterServiceImpl(classLoaderIntegration, classPathPrefix, datomicIntegration)
  }

}
