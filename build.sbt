scalaVersion in ThisBuild := "2.12.3"
organization in ThisBuild := "io.ggtour"
version in ThisBuild := "1.0.0"

// GLOBAL SETTINGS
scalaSource in Compile := baseDirectory.value / "src"

// PLUGINS
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.2.1")

// PROJECTS
// library + model projects
import ProjectTypes._
import TaskKeys._

lazy val common = project
  .in(file("./common"))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick" % "3.3.2",
      "com.typesafe" % "config" % "1.4.0",
      "io.spray" %% "spray-json" % "1.3.4",
      "com.github.tminglei" %% "slick-pg" % "0.18.1",
      "com.github.tminglei" %% "slick-pg_joda-time" % "0.18.1",
      "com.github.tminglei" %% "slick-pg_spray-json" % "0.18.1",
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    )
  )

lazy val account = project
  .in(file("./account"))
  .dependsOn(common)

lazy val game = project
  .in(file("./game"))
  .settings(
    libraryDependencies ++= Seq(
      "io.spray" %% "spray-json" % "1.3.4"
    )
  )
  .dependsOn(common)

lazy val ladder = project
  .in(file("./ladder"))
  .settings(
    libraryDependencies ++= Seq(
      "com.github.forwardloop" % "glicko2s_2.11" % "0.9.4"
    )
  )
  .dependsOn(core, account)

lazy val discord = project
  .in(file("./discord"))
  .settings(
    libraryDependencies ++= Seq(
      "net.katsstuff" %% "ackcord-core" % "0.15.0",
      "net.katsstuff" %% "ackcord-commands-core" % "0.15.0",
    )
  )
  .dependsOn(core, account)

// service projects
lazy val core = project
  .in(file("./core"))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % "2.6.1",
      "com.typesafe.akka" %% "akka-cluster-typed" % "2.6.1",
      "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % "1.0.5",
      "com.typesafe.akka" %% "akka-discovery" % "2.6.1",
      "net.debasishg" %% "redisclient" % "3.20"
    ),
  )
  .dependsOn(common, game)

lazy val ladderService = serviceProject("ladderService")
  .in(file("./ladder"))
  .dependsOn(core, ladder)

lazy val discordService = serviceProject("discordService")
  .in(file("./discord"))
  .dependsOn(core, discord)

reStartSandbox in ThisBuild := {
  reStopSandbox.value
  println("Starting sandbox...")
}

reStopSandbox in ThisBuild := {
  println("Stopping sandbox...")
}