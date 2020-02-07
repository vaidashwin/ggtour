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
      "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2",
      "com.typesafe" % "config" % "1.4.0",
      "io.spray" %% "spray-json" % "1.3.4",
      "com.github.tminglei" %% "slick-pg" % "0.18.1",
      "com.github.tminglei" %% "slick-pg_joda-time" % "0.18.1",
      "com.github.tminglei" %% "slick-pg_spray-json" % "0.18.1",
      "ch.qos.logback" % "logback-core" % "1.2.3",
      "org.slf4j" % "slf4j-simple" % "1.6.4",
      "org.postgresql" % "postgresql" % "9.4.1209",
      "org.flywaydb" % "flyway-core" % "6.2.1"
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
  .dependsOn(common, account, game)

lazy val discord = dependencyProject("discord")
  .in(file("./discord"))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream-typed" % "2.6.0",
      "net.katsstuff" %% "ackcord-core" % "0.15.0",
      "net.katsstuff" %% "ackcord-commands-core" % "0.15.0",
    )
  )
  .dependsOn(common, account)

// service projects
lazy val core = dependencyProject("core")
  .in(file("./core"))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % "2.6.0",
      "net.debasishg" %% "redisclient" % "3.20"
    ),
  )
  .dependsOn(common, account, game, ladder, discord)

lazy val accountService = serviceProject("accountService", "io.ggtour.core.service.AccountService")
  .in(file("./account"))
  .dependsOn(core)

lazy val ladderService = serviceProject("ladderService", "io.ggtour.ladder.service.LadderService")
  .in(file("./ladder"))
  .dependsOn(core)

lazy val discordService = serviceProject("discordService", "io.ggtour.ladder.service.DiscordService")
  .in(file("./discord"))
  .dependsOn(core)

lazy val webapp = serviceProject("webapp", "io.ggtour.webapp.GGTour")
  .in(file("./webapp"))
  .settings(
    baseDirectory := file(".") / "webapp"
  )
  .dependsOn(core)

// Sandbox project; add service projects to its dependencies.
import _root_.io.github.davidmweber.FlywayPlugin
import _root_.io.github.davidmweber.FlywayPlugin.autoImport._
import Keys._
import com.typesafe.config.ConfigFactory

lazy val ggtour = project
    .in(file("."))
    .dependsOn(accountService, ladderService, discordService, webapp)
    .enablePlugins(FlywayPlugin)
    .settings(
      appConfig := {
        ConfigFactory.parseFile((resourceDirectory in Compile in core).value / "application-shared.conf").resolve()
      },
      // Flyway settings
      flywayUrl := appConfig.value.getString("postgres.properties.url"),
      flywayUser := appConfig.value.getString("postgres.properties.user"),
      flywayPassword := appConfig.value.getString("postgres.properties.password"),
      flywayLocations := Seq("classpath:io.ggtour.db.migrations"),

      // Sandbox app settings
      mainClass in reStart := Some("io.ggtour.Sandbox")
    )