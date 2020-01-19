import TaskKeys._
import sbt.Keys._
import sbt._

object ProjectTypes {
  sealed class ServiceProject(name: String) {
    def in(file: File): Project =
      Project(name, file)
        .settings(
          reStop := {
            println(s"Stopping service $name")
          },
          reStart := {
            reStop.value
            println(s"Starting service $name")
          },

          baseDirectory := baseDirectory.value / "service"
        )
  }
  def serviceProject(name: String): ServiceProject = new ServiceProject(name)
}
