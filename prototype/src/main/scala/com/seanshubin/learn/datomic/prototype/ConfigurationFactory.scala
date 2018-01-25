package com.seanshubin.learn.datomic.prototype

trait ConfigurationFactory {
  def validate(args: Seq[String]): Either[Seq[String], Configuration]
}
