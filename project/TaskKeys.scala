import sbt._

object TaskKeys {
  val reStart = taskKey[Unit]("(Re)start the service, compiling and packaging if necessary.")
  val reStop = taskKey[Unit]("Stop the service.")
}
