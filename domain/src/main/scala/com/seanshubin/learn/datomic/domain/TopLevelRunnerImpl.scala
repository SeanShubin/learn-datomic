package com.seanshubin.learn.datomic.domain

class TopLevelRunnerImpl(args: Seq[String],
                         configurationValidator: ConfigurationValidator,
                         notifications: Notifications,
                         createConfigurationRunner: Configuration => Runnable) extends Runnable {
  override def run(): Unit = {
    val errorOrConfiguration = configurationValidator.validate(args)
    errorOrConfiguration match {
      case Left(error) =>
        notifications.configurationError(error)
      case Right(theConfiguration) =>
        createConfigurationRunner(theConfiguration).run()
    }
  }
}
