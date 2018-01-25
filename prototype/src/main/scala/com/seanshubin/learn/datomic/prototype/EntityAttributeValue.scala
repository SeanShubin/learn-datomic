package com.seanshubin.learn.datomic.prototype

import java.lang.{Boolean => JavaBoolean, Long => JavaLong}
import java.time.Instant
import java.util.Date

import clojure.lang.Keyword
import com.seanshubin.learn.datomic.prototype.EntityAttributeValue.OrderByEntityAttributeValue

trait EntityAttributeValue extends Ordered[EntityAttributeValue] {
  def entity: Long

  def attribute: Keyword

  def value: AnyRef

  def typeName: String

  override def compare(that: EntityAttributeValue): Int = OrderByEntityAttributeValue.compare(this, that)
}

case class EntityAttributeString(entity: Long, attribute: Keyword, stringValue: String) extends EntityAttributeValue {
  override def toString: String = f"$entity%14d $attribute%-21s $typeName%-8s '$stringValue%s'"

  override def value: AnyRef = stringValue

  override def typeName: String = "string"
}

case class EntityAttributeKeyword(entity: Long, attribute: Keyword, keywordValue: Keyword) extends EntityAttributeValue {
  override def toString: String = f"$entity%14d $attribute%-21s $typeName%-8s  (${keywordValue.getNamespace}%s ${keywordValue.getName}%s)"

  override def value: AnyRef = keywordValue

  override def typeName: String = "keyword"
}

case class EntityAttributeBoolean(entity: Long, attribute: Keyword, booleanValue: Boolean) extends EntityAttributeValue {
  override def toString: String = f"$entity%14d $attribute%-21s $typeName%-8s  $booleanValue%s"

  override def value: AnyRef = booleanValue.asInstanceOf[AnyRef]

  override def typeName: String = "boolean"
}

case class EntityAttributeLong(entity: Long, attribute: Keyword, longValue: Long) extends EntityAttributeValue {
  override def toString: String = f"$entity%14d $attribute%-21s $typeName%-8s  $longValue%s"

  override def value: AnyRef = longValue.asInstanceOf[AnyRef]

  override def typeName: String = "long"
}

case class EntityAttributeDate(entity: Long, attribute: Keyword, dateValue: Instant) extends EntityAttributeValue {
  override def toString: String = f"$entity%14d $attribute%-21s $typeName%-8s  $dateValue%s"

  override def value: AnyRef = dateValue

  override def typeName: String = "date"
}

case class EntityAttributeFunction(entity: Long, attribute: Keyword, functionValue: datomic.function.Function) extends EntityAttributeValue {
  override def toString: String = f"$entity%14d $attribute%-21s $typeName%-8s  $functionValue"

  override def value: AnyRef = functionValue

  override def typeName: String = "function"
}

object EntityAttributeValue {
  def create(entity: Long, attribute: Keyword, value: AnyRef): EntityAttributeValue = {
    value match {
      case stringValue: String => EntityAttributeString(entity, attribute, stringValue)
      case keywordValue: clojure.lang.Keyword => EntityAttributeKeyword(entity, attribute, keywordValue)
      case booleanValue: JavaBoolean => EntityAttributeBoolean(entity, attribute, booleanValue)
      case longValue: JavaLong => EntityAttributeLong(entity, attribute, longValue)
      case dateValue: Date => EntityAttributeDate(entity, attribute, dateValue.toInstant)
      case functionValue: datomic.function.Function => EntityAttributeFunction(entity, attribute, functionValue)
      case unsupportedValue =>
        throw new RuntimeException(s"Values of type ${unsupportedValue.getClass.getName} are not supported")
    }
  }

  object OrderByEntityAttributeValue extends Ordering[EntityAttributeValue] {
    override def compare(x: EntityAttributeValue, y: EntityAttributeValue): Int = {
      Ordering.Tuple3(Ordering.Long, Ordering.String, AnyRefOrdering).compare(
        (x.entity, x.attribute.toString, x.value),
        (y.entity, y.attribute.toString, y.value))
    }
  }

  object OrderByAttributeEntityValue extends Ordering[EntityAttributeValue] {
    override def compare(x: EntityAttributeValue, y: EntityAttributeValue): Int = {
      Ordering.Tuple3(Ordering.String, Ordering.Long, AnyRefOrdering).compare(
        (x.attribute.toString, x.entity, x.value),
        (y.attribute.toString, y.entity, y.value))
    }
  }

}

/*
            20 :fressian/tag         Keyword :ref
            21 :fressian/tag         Keyword :key
            22 :fressian/tag         Keyword :int
            23 :fressian/tag         Keyword :string
            24 :fressian/tag         Keyword :bool
            25 :fressian/tag         Keyword :inst
            26 :fressian/tag         Keyword :datomic/fn
            27 :fressian/tag         Keyword :bytes
            56 :fressian/tag         Keyword :uuid
            57 :fressian/tag         Keyword :double
            58 :fressian/tag         Keyword :float
            59 :fressian/tag         Keyword :uri
            60 :fressian/tag         Keyword :bigint
            61 :fressian/tag         Keyword :bigdec

*/
