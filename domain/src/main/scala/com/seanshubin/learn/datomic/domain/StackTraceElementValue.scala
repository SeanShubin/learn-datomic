package com.seanshubin.learn.datomic.domain

case class StackTraceElementValue(className: String, methodName: String, fileName: String, lineNumber: Int)

object StackTraceElementValue {
  def fromStackTraceElement(stackTraceElement: StackTraceElement):StackTraceElementValue = {
    val className = stackTraceElement.getClassName
    val methodName = stackTraceElement.getMethodName
    val fileName = stackTraceElement.getFileName
    val lineNumber = stackTraceElement.getLineNumber
    StackTraceElementValue(className,methodName,fileName,lineNumber)
  }
}