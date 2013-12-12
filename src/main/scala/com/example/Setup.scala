package com.example

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import com.typesafe.config.ConfigFactory

object CoreSite {
  import scala.xml.XML

  lazy val cfg = (XML.loadFile(System.getenv.get("SPARK_HOME") + "/conf/core-site.xml") \\ "property").map(p => ((p \\ "name").text.toString, (p \\ "value").text.toString)).toMap
  def query(key:String):String = {
    cfg.getOrElse(key, "")
  }

}



object Setup {

  def simpleGetReq(url: String, headers: Map[String, String] = Map()): String = {

  import java.io.OutputStreamWriter
  import java.net.{ URLConnection, URL }
  import java.io.StringWriter
  import org.apache.commons.io.IOUtils

  import collection.JavaConversions._
  import java.net.URLEncoder.encode

  val u = new URL(url)
  val conn = u.openConnection()
  for ((propName, propValue) <- headers) conn.setRequestProperty(propName, propValue)
  conn.connect

  val writer = new StringWriter()
  IOUtils.copy(conn.getInputStream, writer)
  writer.toString()
}


  def fetchState: MesosState = {
    import org.json4s._
    import org.json4s.JsonDSL._
    import org.json4s.native.JsonMethods._
    implicit val formats = DefaultFormats

    val jsonInput = simpleGetReq("http://localhost:5050/master/state.json")
    parse(jsonInput).extract[MesosState]
  }

  def GetSC(projectName: String): SparkContext = {

    val jarName = "%s-assembly-0.0.1-SNAPSHOT.jar".format(projectName)

    if (!isLocal) {
      // This is only relevant if your running on a mesos cluster
      val mesosState = fetchState
      val totalCPUs = mesosState.slaves.foldLeft(0)((counter, slave) => counter + slave.resources.cpus)
      // Recommended article[insert link] Suggests 2-3x number of cpu's mostly
      System.setProperty("spark.default.parallelism", (totalCPUs * 3).toInt.toString)
    } else {
      System.setProperty("spark.default.parallelism", "20")
    }

    System.setProperty("spark.mesos.coarse", "false")
    System.setProperty("spark.storage.blockManagerHeartBeatMs", "300000")

    System.setProperty("spark.akka.threads", "16")

    val envSparkHome = System.getenv.get("SPARK_HOME")
    val sparkHome = if (envSparkHome != null && envSparkHome.length > 1) envSparkHome else "No Root Defined"

    val scalaVersion = scala.util.Properties.versionString.split(" ")(1)

    val jarPath = "target/scala-" + scalaVersion + "/" + jarName
    System.clearProperty("spark.driver.port")
    new SparkContext(master, "Spark Example Context", sparkHome, List(jarPath), Map())
  }


  val master = if (System.getenv.get("MASTER") != null && System.getenv.get("MASTER").length > 1) System.getenv.get("MASTER") else "local[4]"
  val isLocal = master.startsWith("local[")
}
