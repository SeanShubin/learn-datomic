package com.seanshubin.learn.datomic.prototype

import java.nio.charset.Charset
import java.nio.file.Paths

import com.seanshubin.devon.domain.DevonMarshaller
import com.seanshubin.learn.datomic.contract.FilesContract

class ConfigurationFactoryImpl(fileSystem: FilesContract,
                               devonMarshaller: DevonMarshaller,
                               charset: Charset) extends ConfigurationFactory {
  private val sampleConfiguration: Configuration = Configuration(
    greetingTarget = "world"
  )

  override def validate(args: Seq[String]): Either[Seq[String], Configuration] = {
    if (args.length == 1) {
      val configFilePath = Paths.get(args(0))
      try {
        if (fileSystem.exists(configFilePath)) {
          val bytes = fileSystem.readAllBytes(configFilePath)
          val text = new String(bytes.toArray, charset)
          val devon = devonMarshaller.fromString(text)
          val config = devonMarshaller.toValue(devon, classOf[Configuration])
          Right(config)
        } else {
          Left(Seq(s"Configuration file named '$configFilePath' not found"))
        }
      } catch {
        case ex: Throwable =>
          Left(Seq(s"There was a problem reading the configuration file '$configFilePath': ${ex.getMessage}"))
      }
    } else {
      val sampleConfigDevon = devonMarshaller.fromValue(sampleConfiguration)
      val prettySampleLines = devonMarshaller.toPretty(sampleConfigDevon)
      Left(Seq(
        "Expected exactly one argument, the name of the configuration file",
        "A typical configuration file might look something like this",
        "") ++ prettySampleLines)
    }
  }
}
