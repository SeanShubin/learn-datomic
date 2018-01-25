package com.seanshubin.learn.datomic.domain

sealed trait SingleUpdateResult {
  def ruleName: String

  def isSuccess: Boolean

  def appliedPreviously: Boolean

  def appliedJustNow: Boolean

  def unableToApply: Boolean

}

object SingleUpdateResult {

  case class AlreadyApplied(ruleName: String) extends SingleUpdateResult {
    override def isSuccess: Boolean = true

    override def appliedPreviously: Boolean = true

    override def unableToApply: Boolean = false

    override def appliedJustNow: Boolean = false
  }

  case class CouldNotVerify(ruleName: String) extends SingleUpdateResult {
    override def isSuccess: Boolean = false

    override def appliedPreviously: Boolean = false

    override def unableToApply: Boolean = true

    override def appliedJustNow: Boolean = false
  }

  case class ThrewException(ruleName: String, exception: ThrowableValue) extends SingleUpdateResult {
    override def isSuccess: Boolean = false

    override def appliedPreviously: Boolean = false

    override def unableToApply: Boolean = true

    override def appliedJustNow: Boolean = false
  }

  case class Success(ruleName: String) extends SingleUpdateResult {
    override def isSuccess: Boolean = true

    override def appliedPreviously: Boolean = false

    override def unableToApply: Boolean = false

    override def appliedJustNow: Boolean = true
  }

  case class DependenciesFailed(ruleName: String, otherResults: Seq[SingleUpdateResult]) extends SingleUpdateResult {
    override def isSuccess: Boolean = false

    override def appliedPreviously: Boolean = false

    override def unableToApply: Boolean = true

    override def appliedJustNow: Boolean = false
  }

}
