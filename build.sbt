scalaVersion in ThisBuild := "2.12.3"
organization in ThisBuild := "io.ggtour"

// GLOBAL SETTINGS
scalaSource in Compile := baseDirectory.value / "src"

// PROJECTS
// library projects
lazy val game = project
  .in(file("./game"))

lazy val core = project
  .in(file("./core"))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % "2.6.1",
      "com.typesafe.akka" %% "akka-cluster-typed" % "2.6.1",
      "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % "1.0.5",
      "com.typesafe.akka" %% "akka-discovery" % "2.6.1",
      "com.typesafe" % "config" % "1.4.0",
      "net.debasishg" %% "redisclient" % "3.20",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.2.2",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.2.2"

    ),
  )
  .dependsOn(game)

// service projects
lazy val ladder = project
  .in(file("./ladder"))
  .dependsOn(core)