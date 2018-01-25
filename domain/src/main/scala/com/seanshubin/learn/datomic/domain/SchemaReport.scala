package com.seanshubin.learn.datomic.domain

case class SchemaReport(isSuccess: Boolean,
                        appliedPreviously: Seq[SingleUpdateResult],
                        appliedJustNow: Seq[SingleUpdateResult],
                        unableToApply: Seq[SingleUpdateResult])

object SchemaReport {
  def fromUpdateResults(results: Seq[SingleUpdateResult]): SchemaReport = {
    val isSuccess = results.forall(_.isSuccess)
    val appliedPreviously = results.filter(_.appliedPreviously)
    val appliedJustNow = results.filter(_.appliedJustNow)
    val unableToApply = results.filter(_.unableToApply)
    SchemaReport(isSuccess, appliedPreviously, appliedJustNow, unableToApply)
  }
}
