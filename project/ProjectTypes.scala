import sbtassembly.AssemblyKeys._
import sbt.Keys._
import sbt._
import sbtdocker.DockerKeys._
import sbtdocker._
import spray.revolver.RevolverPlugin

object ProjectTypes {
  sealed class ServiceProject(name: String, mainClassFQN: String) {
    def in(file: File): Project =
      Project(name, file)
        .enablePlugins(sbtdocker.DockerPlugin)
        .settings(
          baseDirectory := baseDirectory.value / "service",
          // Assembly settings
          mainClass in assembly := Some(mainClassFQN),

          // Shared Docker settings
          docker := (docker dependsOn assembly).value,
          dockerfile in docker := {
            val artifact = (assemblyOutputPath in assembly).value
            val artifactTarget = s"/app/${artifact.name}"
            new Dockerfile {
              from("openjdk:12")
              add(artifact, artifactTarget)
              entryPoint("java", "-jar", artifactTarget)
            }
          }

        )
  }
  def serviceProject(name: String, mainClass: String): ServiceProject =
    new ServiceProject(name, mainClass)

  sealed class DependencyProject(name: String) {
    def in(file: File): Project =
      Project(name, file)
        .disablePlugins(RevolverPlugin)
  }

  def dependencyProject(name: String): DependencyProject =
    new DependencyProject(name)
}
