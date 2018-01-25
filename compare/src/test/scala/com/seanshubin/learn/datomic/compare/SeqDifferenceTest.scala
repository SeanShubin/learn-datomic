package com.seanshubin.learn.datomic.compare

import org.scalatest.FunSuite

class SeqDifferenceTest extends FunSuite {
  test("same") {
    val a = Seq(1, 2, 3)
    val b = Seq(1, 2, 3)
    val difference = SeqDifference.diff(a, b)
    val expectedMessage =
      """sequences are identical
        |same[0]        = 1
        |same[1]        = 2
        |same[2]        = 3""".stripMargin.split( """\r\n|\r|\n""")
    assert(difference.isSame === true)
    assert(difference.messageLines === expectedMessage)
  }

  test("difference") {
    val a = Seq(1, 2, 3)
    val b = Seq(1, 4, 3)
    val difference = SeqDifference.diff(a, b)
    assert(difference.isSame === false)
    assert(difference.messageLines ===
      """sequences different at index 1
        |same[0]        = 1
        |different-a[1] = 2
        |different-b[1] = 4
        |remaining elements skipped""".stripMargin.split( """\r\n|\r|\n"""))
  }

  test("b shorter") {
    val a = Seq(1, 2, 3)
    val b = Seq(1, 2)
    val difference = SeqDifference.diff(a, b)
    assert(difference.isSame === false)
    assert(difference.messageLines ===
      """sequences different at index 2
        |same[0]        = 1
        |same[1]        = 2
        |different-a[2] = 3
        |different-b[2] = <missing>
        |remaining elements skipped""".stripMargin.split( """\r\n|\r|\n"""))
  }
  test("a shorter") {
    val a = Seq(1, 2, 3)
    val b = Seq(1, 2, 3, 4)
    val difference = SeqDifference.diff(a, b)
    assert(difference.isSame === false)
    assert(difference.messageLines ===
      """sequences different at index 3
        |same[0]        = 1
        |same[1]        = 2
        |same[2]        = 3
        |different-a[3] = <missing>
        |different-b[3] = 4
        |remaining elements skipped""".stripMargin.split( """\r\n|\r|\n"""))
  }
}
