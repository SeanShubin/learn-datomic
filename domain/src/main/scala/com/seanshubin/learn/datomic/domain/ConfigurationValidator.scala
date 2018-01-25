package com.seanshubin.learn.datomic.domain

trait ConfigurationValidator {
  def validate(args: Seq[String]): Either[Seq[String], Configuration]
}
