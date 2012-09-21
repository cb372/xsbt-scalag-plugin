package scalag

import org.fusesource.scalate._
import util.FileResourceLoader
import java.io.File

object Scalate {

  private def makeEngine(classpath: Seq[File]): TemplateEngine = {
    val engine = new TemplateEngine

    // Weird Scalate classpath bug. Amazingly this fixes it!
    // see https://groups.google.com/d/topic/scalate/sXrLmjRbrbw/discussion
    val scalaLibraryJar = classpath.find(_.getName == "scala-library.jar")
    scalaLibraryJar.foreach { jar =>
      engine.classpath = jar.getAbsolutePath
      engine.combinedClassPath = true
    }

    engine
  }

  /**
   * Use the given Scalate template and values map to generate a string.
   * @param templatePath can be either a file path or a classpath resource path
   * @param values key-value map used to subsitute values in the template
   * @param classpath SBT's managed classpath
   * @return
   */
  def scalate(templatePath: String, values: Map[String, Any], classpath: Seq[File]): String = {
    val engine = makeEngine(classpath)

    // load template from file or classpath
    val source = TemplateSource.fromUri(templatePath, engine.resourceLoader)

    // run Scalate with the given template and values
    engine.layout(source, values)
  }
}