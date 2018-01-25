package com.seanshubin.learn.datomic.compare

import org.scalatest.FunSuite

class SetDifferenceTest extends FunSuite {
  test("same") {
    val a = Set(123)
    val b = Set(123)
    val difference = SetDifference.diff(a, b)
    val expectedMessage =
      """Sets are identical
        |  123""".stripMargin.split( """\r\n|\r|\n""")
    assert(difference.isSame === true)
    assert(difference.messageLines === expectedMessage)
  }

  test("difference") {
    val a = Set(1, 2, 3)
    val b = Set(1, 4, 3)
    val difference = SetDifference.diff(a, b)
    assert(difference.isSame === false)
    assert(difference.messageLines ===
      """Sets are different
        |in a but not in b
        |  2
        |in b but not in a
        |  4""".stripMargin.split( """\r\n|\r|\n"""))
  }

  test("b shorter") {
    val a = Set(1, 2, 3)
    val b = Set(1, 2)
    val difference = SetDifference.diff(a, b)
    assert(difference.isSame === false)
    assert(difference.messageLines ===
      """Sets are different
        |in a but not in b
        |  3""".stripMargin.split( """\r\n|\r|\n"""))
  }
  test("a shorter") {
    val a = Set(1, 2, 3)
    val b = Set(1, 2, 3, 4)
    val difference = SetDifference.diff(a, b)
    assert(difference.isSame === false)
    assert(difference.messageLines ===
      """Sets are different
        |in b but not in a
        |  4""".stripMargin.split( """\r\n|\r|\n"""))
  }
}
