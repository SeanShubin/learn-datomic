package com.seanshubin.learn.datomic.domain

import clojure.lang.Keyword
import com.seanshubin.learn.datomic.domain.DatomWrapper.OrderByEntityAttributeValue
import com.seanshubin.learn.datomic.domain.DatomicPrimitive.ValueWrapper

case class DatomWrapper(entity: Long, attribute: Keyword, value: ValueWrapper) extends Ordered[DatomWrapper] {
  override def compare(that: DatomWrapper): Int = OrderByEntityAttributeValue.compare(this, that)

  override def toString: String = f"$entity%14s $attribute%-21s $value%s"
}

object DatomWrapper {

  object OrderByEntityAttributeValue extends Ordering[DatomWrapper] {
    override def compare(x: DatomWrapper, y: DatomWrapper): Int = {
      Ordering.Tuple3(Ordering.Long, KeywordOrdering, ValueWrapperOrdering).compare(
        (x.entity, x.attribute, x.value),
        (y.entity, y.attribute, y.value))
    }
  }

  object OrderByAttributeEntityValue extends Ordering[DatomWrapper] {
    override def compare(x: DatomWrapper, y: DatomWrapper): Int = {
      Ordering.Tuple3(KeywordOrdering, Ordering.Long, ValueWrapperOrdering).compare(
        (x.attribute, x.entity, x.value),
        (y.attribute, y.entity, y.value))
    }
  }

  object KeywordOrdering extends Ordering[Keyword] {
    override def compare(x: Keyword, y: Keyword): Int = Ordering.String.compare(x.toString, y.toString)
  }

  object ValueWrapperOrdering extends Ordering[ValueWrapper] {
    override def compare(x: ValueWrapper, y: ValueWrapper): Int = {
      val typeCompareResult = KeywordOrdering.compare(x.theType.keyword, y.theType.keyword)
      if (typeCompareResult == 0) {
        x.theType.compare(x.value, y.value)
      } else {
        typeCompareResult
      }
    }
  }

}