package io.ggtour.core.service

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Scheduler}
import akka.actor.typed.scaladsl.adapter._
import akka.util.Timeout
import akka.actor.typed.scaladsl.AskPattern._
import io.ggtour.common.service.GGMessage
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Future

// Shared logic for entry point for each node in the cluster. Each service should
// implement this class.
abstract class ServiceNode[T <: GGMessage](val serviceActor: ServiceActor[T]) extends App {
  val actorSystem: ActorSystem[GGMessage] =
    ActorSystem(GGTourBehavior(serviceActor), s"ggtour-${serviceActor.serviceBaseName}-system")

  def apply(): ActorRef[GGMessage] = actorSystem
  def system(): ActorSystem[GGMessage] = actorSystem
}

object GGTourBehavior {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  def apply[T <: GGMessage](service: ServiceActor[T]): Behavior[GGMessage] = Behaviors.receive {
    case (context, message: T) =>
      logger.debug("Received message: {}", message)
      context.spawn(service.serviceBehavior, service.getActorName) ! message
      Behaviors.same
  }
}
