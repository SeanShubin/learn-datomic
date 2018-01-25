package com.seanshubin.learn.datomic.compare

object SeqDifference {
  def diff[T](a: Iterable[T], b: Iterable[T]): DifferenceResult = {
    def buildSeqDifference(index: Int, soFar: List[T], remainA: List[T], remainB: List[T]): DifferenceResult = {
      def composeSeqDifference(a: String, b: String): DifferenceResult = {
        val heading = s"sequences different at index $index"
        val sameMessage = soFar.reverse.zipWithIndex.map(composeSameAtIndex)
        val differenceMessage = composeDifferentAtIndex(index, a, b)
        val messageLines = Seq(heading) ++ sameMessage ++ differenceMessage ++ Seq("remaining elements skipped")
        val isSame = false
        DifferenceResult(isSame, messageLines)
      }
      (remainA, remainB) match {
        case (headA :: tailA, headB :: tailB) =>
          if (headA == headB) {
            buildSeqDifference(index + 1, headA :: soFar, tailA, tailB)
          } else {
            composeSeqDifference(headA.toString, headB.toString)
          }
        case (Nil, headB :: tailB) =>
          composeSeqDifference("<missing>", headB.toString)
        case (headA :: tailA, Nil) =>
          composeSeqDifference(headA.toString, "<missing>")
        case (Nil, Nil) =>
          val isSame = true
          val messageLines = Seq("sequences are identical") ++ soFar.reverse.zipWithIndex.map(composeSameAtIndex)
          DifferenceResult(isSame, messageLines)
      }
    }
    buildSeqDifference(0, Nil, a.toList, b.toList)
  }

  private def composeSameAtIndex[T](valueAndIndex: (T, Int)): String = {
    val (value, index) = valueAndIndex
    s"same[$index]        = $value"
  }

  private def composeDifferentAtIndex(index: Int, a: String, b: String): Seq[String] = {
    Seq(s"different-a[$index] = $a", s"different-b[$index] = $b")
  }
}
