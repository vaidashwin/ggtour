package main.scala.io.ggtour

import akka.actor.typed.Scheduler
import akka.util.Timeout
import io.ggtour.core.service._

import scala.concurrent.Future

class SandboxServiceClient(servicesMap: Map[String, ServiceNode[_]])
    extends ServiceClientImpl {
  override def !(message: GGMessage): Unit =
    servicesMap.get(message.service).foreach(_ ! message)
  override def ?[T](message: GGRequest[T])(
      implicit timeout: Timeout,
      scheduler: Scheduler): Future[T] =
    servicesMap
      .get(message.service)
      .map(_ ? message)
      .getOrElse(Future.failed(new RuntimeException(
        s"Service ${message.service} does not exist in sandbox.")))
}
