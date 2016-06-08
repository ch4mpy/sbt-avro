import sbt._
import Keys._

object build extends Build {
  val sbtAvro = Project(
    id = "sbt-avro",
      base = file("."),
      settings = Defaults.defaultSettings ++ Seq[Project.Setting[_]](
        organization := "com.c4soft",
        version := "1.1.1",
        sbtPlugin := true,
        scalaVersion := "2.10.4",
        libraryDependencies ++= Seq(
          "org.apache.avro" % "avro" % "1.7.7",
          "org.apache.avro" % "avro-compiler" % "1.7.7",
          "org.specs2" %% "specs2-core" % "3.6.4" % "test"
        ),
        scalacOptions in Compile ++= Seq("-deprecation"),
        description := "Sbt plugin for compiling Avro sources",
        publishTo := Some(if(isSnapshot.value) teadsRepo("snapshots") else teadsRepo("releases")),
        publishMavenStyle := true,
        credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
      )
    )

    private def teadsRepo(name: String) =
      s"Teads $name" at s"http://nexus.teads.net/content/repositories/$name/"
}
