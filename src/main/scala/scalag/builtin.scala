package scalag

/**
 * Scalag built-in commands
 */
object builtin {

  // -----------------
  // class
  // -----------------

  val classCommand: ScalagCommand = ScalagCommand(
    namespace = "class",
    args = Seq("FQCN"),
    description = "Generates a new class file",
    operation = {
      case ScalagInput("class" :: fqcn :: _, settings) =>
        val _fqcn = FQCN(fqcn)
        FilePath(settings.srcDir + "/" + _fqcn.filepath).writeIfNotExists(
          ftl2string(path = "templates/builtin/class.ftl",
            values = Map("packageName" -> _fqcn.packageName, "name" -> _fqcn.className))
        )
    }
  )

  // -----------------
  // object
  // -----------------

  val objectCommand: ScalagCommand = ScalagCommand(
    namespace = "object",
    args = Seq("FQCN"),
    description = "Generates a new object file",
    operation = {
      case ScalagInput("object" :: fqcn :: _, settings) =>
        val _fqcn = FQCN(fqcn)
        FilePath(settings.srcDir + "/" + _fqcn.filepath).writeIfNotExists(
          ftl2string(path = "templates/builtin/object.ftl",
            values = Map("packageName" -> _fqcn.packageName, "name" -> _fqcn.className))
        )
    }
  )

  // -----------------
  // specs2
  // -----------------

  private[this] def writeSpecs2IfNotExists(settings: SbtSettings, fqcn: FQCN, style: String): Unit = {
    try {
      FilePath(settings.testDir + "/" + fqcn.specFilepath).writeIfNotExists(
        ftl2string(path = "templates/builtin/specs2-" + style + ".ftl",
          values = Map("packageName" -> fqcn.packageName, "name" -> fqcn.className)
        ))
    } catch {
      case e => specs2Command.help.showUsage()
    }
  }

  val specs2Command: ScalagCommand = ScalagCommand(
    namespace = "specs2",
    args = Seq("FQCN", """"unit"/"acceptance""""),
    description = "Generates a new spec2 file for the specified class",
    operation = {
      case ScalagInput("specs2" :: fqcn :: style :: Nil, settings) => writeSpecs2IfNotExists(settings, FQCN(fqcn), style)
      case ScalagInput("specs2" :: fqcn :: Nil, settings) => writeSpecs2IfNotExists(settings, FQCN(fqcn), "unit")
    }
  )

  // -----------------
  // ScalaTest
  // -----------------

  private[this] def writeScalaTestIfNotExists(settings: SbtSettings, fqcn: FQCN, style: String): Unit = {
    try {
      val content = ftl2string(path = "templates/builtin/ScalaTest-" + style + ".ftl",
        values = Map("packageName" -> fqcn.packageName, "name" -> fqcn.className)
      )
      style match {
        case "FunSuite" => FilePath(settings.testDir + "/" + fqcn.suiteFilepath).writeIfNotExists(content)
        case _ => FilePath(settings.testDir + "/" + fqcn.specFilepath).writeIfNotExists(content)
      }
    } catch {
      case e => ScalaTestCommand.help.showUsage()
    }
  }

  val ScalaTestCommand: ScalagCommand = ScalagCommand(
    namespace = "ScalaTest",
    args = Seq("FQCN", """""FunSuite"/"Spec"/"WordSpec"/"FlatSpec"/"FeatureSpec"""""),
    description = "Generates a new ScalaTest file for the specified class",
    operation = {
      case ScalagInput("ScalaTest" :: fqcn :: style :: Nil, settings) => writeScalaTestIfNotExists(settings, FQCN(fqcn), style)
      case ScalagInput("ScalaTest" :: fqcn :: Nil, settings) => writeScalaTestIfNotExists(settings, FQCN(fqcn), "FlatSpec")
    }
  )

}
