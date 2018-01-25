package com.seanshubin.learn.datomic.domain

class SchemaUpdaterLogicImpl(service: SchemaUpdaterService, notifications: Notifications) extends SchemaUpdaterLogic {
  override def findAndApplyAll(): SchemaReport = {
    val ruleNames = service.findRuleNames()
    val updateResults = ruleNames.foldLeft(Seq[SingleUpdateResult]())(applyRule)
    SchemaReport.fromUpdateResults(updateResults)
  }

  private def applyRule(soFar: Seq[SingleUpdateResult], currentRuleName: String): Seq[SingleUpdateResult] = {
    if (soFar.map(_.ruleName).contains(currentRuleName)) soFar
    else if (service.alreadyApplied(currentRuleName)) {
      val result = SingleUpdateResult.AlreadyApplied(currentRuleName)
      notifications.schemaUpdateResult(result)
      result +: soFar
    } else {
      val dependencies = service.dependencies(currentRuleName)
      val dependencyResults = dependencies.foldLeft(soFar)(applyRule)
      val dependencyFailures = dependencyResults.filterNot(_.isSuccess)
      val result = if (dependencyFailures.isEmpty) {
        try {
          service.apply(currentRuleName)
          if (service.alreadyApplied(currentRuleName)) {
            SingleUpdateResult.Success(currentRuleName)
          } else {
            SingleUpdateResult.CouldNotVerify(currentRuleName)
          }
        } catch {
          case ex: Exception =>
            SingleUpdateResult.ThrewException(currentRuleName, ThrowableValue.fromThrowable(ex))
        }
      } else {
        SingleUpdateResult.DependenciesFailed(currentRuleName, dependencyFailures)
      }
      notifications.schemaUpdateResult(result)
      Seq(result) ++ dependencyResults ++ soFar
    }
  }
}
