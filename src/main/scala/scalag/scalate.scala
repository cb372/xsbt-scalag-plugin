package scalag

import org.fusesource.scalate._
import util.FileResourceLoader

object Scalate {
  val engine = new TemplateEngine

  // Weird Scalate classpath bug. Amazingly this fixes it!
  // see https://groups.google.com/d/topic/scalate/sXrLmjRbrbw/discussion
  engine.classpath = "/Users/chris/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-2.9.2.jar"
  engine.combinedClassPath = true

  def scalate(path: String, values: Map[String, Any]): String = {
    // load template from classpath
    val source = TemplateSource.fromUri(path, engine.resourceLoader)

    // run Scalate with the given template and values
    engine.layout(source, values)
  }
}
