package com.seanshubin.learn.datomic.domain

case class SeqComparison(same: Boolean, messageLines: Seq[String])

object SeqComparison {
  def compare(left: Seq[String], right: Seq[String]): SeqComparison = {
    compareRecursive(left.toList, right.toList, 0, Nil)
  }

  private def compareRecursive(actualSeq: List[String], expectedSeq: List[String], index: Int, messageLines: List[String]): SeqComparison = {
    (actualSeq, expectedSeq) match {
      case (Nil, Nil) => SeqComparison(same = true, messageLines)
      case (actualHead :: actualTail, Nil) =>
        val newMessageLines = messageLines.reverse ++ Seq(
          s"extra at $index",
          actualHead)
        SeqComparison(same = false, newMessageLines)
      case (Nil, expectedHead :: expectedTail) =>
        val newMessageLines = messageLines.reverse ++ Seq(
          s"missing at $index",
          expectedHead)
        SeqComparison(same = false, newMessageLines)
      case (actualHead :: actualTail, expectedHead :: expectedTail) =>
        if (actualHead == expectedHead) {
          compareRecursive(actualTail, expectedTail, index + 1, s"same[$index]: $actualHead" :: messageLines)
        } else {
          val newMessageLines = messageLines.reverse ++ Seq(
            s"different at $index",
            s"expected: $expectedHead",
            s"actual  : $actualHead")
          SeqComparison(same = false, newMessageLines)
        }
    }
  }
}
