package com.seanshubin.learn.datomic.prototype

import java.lang.{Long => JavaLong}

object AnyRefOrdering extends Ordering[AnyRef] {

  /*
  Long, String, Keyword, Boolean, Date
   */
  override def compare(untypedX: AnyRef, untypedY: AnyRef): Int = {
    (untypedX, untypedY) match {
      case (x: JavaLong, y: JavaLong) => x.compareTo(y)
      case _ =>
        throw new RuntimeException(s"Unable to compare ${untypedX.getClass.getName} with ${untypedY.getClass.getName}")
    }
  }
}
