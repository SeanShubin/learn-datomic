package com.seanshubin.learn.datomic.domain

class SchemaUpdaterServiceImpl(classLoader: ClassLoaderIntegration,
                               classPathPrefix: String,
                               datomicIntegration: DatomicIntegration) extends SchemaUpdaterService {
  override def findRuleNames(): Seq[String] = {
    val ruleNames = loadResourceAsLinesOrEmpty("table-of-contents.txt")
    ruleNames
  }

  override def alreadyApplied(name: String): Boolean = {
    queryExists(name + "/exists.edn")
  }

  override def apply(name: String): Unit = {
    execute(name + "/apply.edn")
  }

  override def dependencies(name: String): Seq[String] = {
    val dependencies = loadResourceAsLinesOrEmpty(name + "/dependencies.txt")
    dependencies
  }

  private def loadResourceAsString(name: String): String = {
    val resourceName = classPathPrefix + name
    val text = InputStreamUtil.ensureClose(classLoader.getResourceAsStream(resourceName)) { inputStream =>
      InputStreamUtil.inputStreamToString(inputStream)
    }
    text
  }

  private def loadResourceAsLinesOrEmpty(name: String): Seq[String] = {
    val resourceName = classPathPrefix + name
    val lines: Seq[String] = classLoader.getResourceAsMaybeStream(resourceName) match {
      case Some(inputStream) =>
        val text = InputStreamUtil.ensureClose(classLoader.getResourceAsStream(resourceName)) { inputStream =>
          InputStreamUtil.inputStreamToString(inputStream)
        }
        text.split( """\r\n|\r|\n""")
      case None => Seq()
    }
    lines
  }

  private def queryExists(source: String): Boolean = {
    val edn = loadResourceAsString(source)
    try {
      datomicIntegration.queryExists(edn)
    } catch {
      case ex: Exception =>
        throw new RuntimeException(source + ": " + ex.getMessage)
    }
  }

  private def execute(source: String): Unit = {
    val edn = loadResourceAsString(source)
    try {
      datomicIntegration.execute(edn)
    } catch {
      case ex: Exception =>
        throw new RuntimeException(source + ": " + ex.getMessage)
    }
  }
}
