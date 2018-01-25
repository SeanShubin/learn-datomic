package com.seanshubin.learn.datomic.domain

case class ThrowableValue(message:String, cause:Option[ThrowableValue], stackTrace:Seq[StackTraceElementValue])

object ThrowableValue {
  def fromThrowable(throwable:Throwable):ThrowableValue = {
    val message = throwable.getMessage
    val cause = Option(throwable.getCause).map(fromThrowable)
    val stackTrace = throwable.getStackTrace.map(StackTraceElementValue.fromStackTraceElement)
    ThrowableValue(message, cause, stackTrace)
  }
}