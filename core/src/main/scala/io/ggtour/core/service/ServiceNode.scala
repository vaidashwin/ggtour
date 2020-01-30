package io.ggtour.core.service

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior, Scheduler}
import akka.actor.typed.scaladsl.adapter._
import akka.util.Timeout
import akka.actor.typed.scaladsl.AskPattern._

import scala.concurrent.Future

// Shared logic for entry point for each node in the cluster. Each service should
// implement this class.
abstract class ServiceNode[T <: GGMessage](val serviceActor: ServiceActor[T]) extends App {
  val actorSystem: ActorSystem[GGMessage] =
    ActorSystem(GGTourBehavior(serviceActor), s"ggtour-${serviceActor.serviceBaseName}-system")

  def ! (message: GGMessage): Unit = actorSystem ! message
  def ?[V] (message: GGRequest[V])(implicit timeout: Timeout, scheduler: Scheduler): Future[V] =
    actorSystem.ask(_ => message)
}

object GGTourBehavior {
  def apply[T <: GGMessage](service: ServiceActor[T]): Behavior[GGMessage] = Behaviors.receive {
    case (context, message: T) =>
      context.spawn(service.serviceBehavior, service.getActorName) ! message
      Behaviors.same
  }
}
