import sbt._
import Keys._
import tv.teads.build.JarPublishingPlugin

object build extends Build {
    val sbtAvro = Project(
        id = "sbt-avro",
        base = file("."),
        settings = Defaults.defaultSettings ++ Seq[Project.Setting[_]](
            organization := "com.c4soft",
            version := "1.0.1",
            sbtPlugin := true,
            scalaVersion := appConfiguration.value.provider.scalaProvider.version,
            libraryDependencies ++= Seq(
                    "org.apache.avro" % "avro" % "1.7.7",
                    "org.apache.avro" % "avro-compiler" % "1.7.7",
                    "org.specs2" %% "specs2-core" % "3.6.4" % "test"
            ),
            scalacOptions in Compile ++= Seq("-deprecation"),
            description := "Sbt plugin for compiling Avro sources",
            publishMavenStyle := true
        )
    ).enablePlugins(JarPublishingPlugin)
}
