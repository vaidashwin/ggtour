import sbt._

object TaskKeys {
  val reStart = taskKey[Unit]("(Re)start the service, compiling and packaging if necessary.")
  val reStop = taskKey[Unit]("Stop the service.")

  val reStartSandbox = taskKey[Unit]("Build and deploy a local cluster for testing.")
  val reStopSandbox = taskKey[Unit]("Stop the running sandbox cluster.")
}
