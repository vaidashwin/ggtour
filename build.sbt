name in ThisBuild := "ggtour"
scalaVersion in ThisBuild := "2.12.3"
organization in ThisBuild := "io.ggtour"
version in ThisBuild := "1.0.0"

// GLOBAL SETTINGS
scalaSource in Compile := baseDirectory.value / "src"

// PROJECTS
// library + model projects
import ProjectTypes._

lazy val common = dependencyProject("common")
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

lazy val account = dependencyProject("account")
  .in(file("./account"))
  .dependsOn(common)

lazy val game = dependencyProject("game")
  .in(file("./game"))
  .settings(
    libraryDependencies ++= Seq(
      "io.spray" %% "spray-json" % "1.3.4"
    )
  )
  .dependsOn(common)

lazy val ladder = dependencyProject("ladder")
  .in(file("./ladder"))
  .settings(
    libraryDependencies ++= Seq(
      "com.github.forwardloop" % "glicko2s_2.11" % "0.9.4"
    )
  )
  .dependsOn(core, account)

lazy val discord = dependencyProject("discord")
  .in(file("./discord"))
  .settings(
    libraryDependencies ++= Seq(
      "net.katsstuff" %% "ackcord-core" % "0.15.0",
      "net.katsstuff" %% "ackcord-commands-core" % "0.15.0",
    )
  )
  .dependsOn(core, account)

// service projects
lazy val core = dependencyProject("core")
  .in(file("./core"))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % "2.6.0",
      "com.typesafe.akka" %% "akka-cluster-typed" % "2.6.0",
      "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % "1.0.5",
      "com.typesafe.akka" %% "akka-discovery" % "2.6.0",
      "net.debasishg" %% "redisclient" % "3.20"
    ),
  )
  .dependsOn(common, game)

lazy val ladderService = serviceProject("ladderService", "io.ggtour.ladder.service.LadderService")
  .in(file("./ladder"))
  .dependsOn(core, ladder)

lazy val discordService = serviceProject("discordService", "io.ggtour.ladder.service.DiscordService")
  .in(file("./discord"))
  .dependsOn(core, discord)

// Sandbox project; add service projects to its dependencies.
lazy val ggtour = project
    .in(file("."))
    .dependsOn(ladderService, discordService)
    .settings(
      mainClass in reStart := Some("io.ggtour.Sandbox")
    )