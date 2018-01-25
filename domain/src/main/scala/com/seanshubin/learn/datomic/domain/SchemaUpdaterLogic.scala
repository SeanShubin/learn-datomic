package com.seanshubin.learn.datomic.domain

trait SchemaUpdaterLogic {
  def findAndApplyAll(): SchemaReport
}
