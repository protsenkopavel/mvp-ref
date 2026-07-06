ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.4"

lazy val root = (project in file("."))
  .settings(
    name := "mvp-fer",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "cask" % "0.10.2",                     // HTTP-сервер и роутинг
      "com.lihaoyi" %% "upickle" % "4.2.1",                   // JSON-сериализация
      "io.swagger.core.v3" % "swagger-annotations" % "2.2.22", // описания полей DTO
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5", // API логирования (slf4j)
      "ch.qos.logback" % "logback-classic" % "1.5.6"          // бэкенд логирования
    )
  )
