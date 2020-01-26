package main.scala.io.ggtour

import akka.actor.typed.{ActorSystem, Scheduler}
import io.ggtour.core.service.{GGMessage, GGRequest, ServiceClientImpl}

import scala.concurrent.Future
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

class SandboxServiceClient(actorSystem: ActorSystem[GGMessage]) extends ServiceClientImpl {
  override def !(message: GGMessage): Unit = actorSystem ! (message)
  override def ?[T](message: GGRequest[T])(implicit timeout: Timeout, scheduler: Scheduler): Future[T] =
    actorSystem.ask(_ => message)
}
