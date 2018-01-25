package com.seanshubin.learn.datomic.domain

import org.scalatest.FunSuite

class WithCommandLineArgsImplTest extends FunSuite {
  /*
    test("valid configuration") {
      val helper = new Helper(validationResult = Right(Configuration("world")))
      helper.launcher.apply()
      assert(helper.sideEffects.size === 2)
      assert(helper.sideEffects(0) ===("notifications.effectiveConfiguration", Configuration("world")))
      assert(helper.sideEffects(1) ===("runner.run", ()))
    }

    test("invalid configuration") {
      val helper = new Helper(validationResult = Left(Seq("error")))
      helper.launcher.apply()
      assert(helper.sideEffects.size === 1)
      assert(helper.sideEffects(0) ===("notifications.configurationError", Seq("error")))
    }

    class Helper(validationResult: Either[Seq[String], Configuration]) {
      val sideEffects: ArrayBuffer[(String, Any)] = new ArrayBuffer()
      val configurationFactory = new FakeConfigurationFactory(Seq("foo.txt"), validationResult)
      val runner = new FakeRunner(sideEffects)
      val createRunnerStub = (configuration: Configuration) => runner
      val notifications = new FakeNotification(sideEffects)
      val launcher = new WithCommandLineArgsImpl(Seq("foo.txt"), configurationFactory, createRunnerStub, notifications)
    }

    class FakeConfigurationFactory(expectedArgs: Seq[String], result: Either[Seq[String], Configuration]) extends ConfigurationFactory {
      override def validate(args: Seq[String]): Either[Seq[String], Configuration] = {
        assert(args === expectedArgs)
        result
      }
    }

    class FakeNotification(sideEffects: ArrayBuffer[(String, Any)]) extends Notifications {
      def append(name: String, value: Any): Unit = {
        sideEffects.append(("notifications." + name, value))
      }

      override def configurationError(lines: Seq[String]): Unit = append("configurationError", lines)

      override def effectiveConfiguration(configuration: Configuration): Unit = append("effectiveConfiguration", configuration)

      override def topLevelException(exception: Throwable): Unit = append("topLevelException", exception)
    }

    class FakeRunner(sideEffects: ArrayBuffer[(String, Any)]) extends Runner {
      override def run(): Unit = sideEffects.append(("runner.run", ()))
    }
  */
}
