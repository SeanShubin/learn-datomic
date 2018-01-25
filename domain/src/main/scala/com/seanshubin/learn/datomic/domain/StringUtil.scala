package com.seanshubin.learn.datomic.domain

object StringUtil {
  def escape(target: String) = {
    target.flatMap {
      case '\n' => "\\n"
      case '\b' => "\\b"
      case '\t' => "\\t"
      case '\f' => "\\f"
      case '\r' => "\\r"
      case '\"' => "\\\""
      case '\'' => "\\\'"
      case '\\' => "\\\\"
      case x => x.toString
    }
  }

  def doubleQuote(target: String) = s""""${escape(target)}""""
}
