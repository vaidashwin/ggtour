package io.ggtour.core.service

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import akka.actor.typed.scaladsl.adapter._

// Shared logic for entry point for each node in the cluster. Each service should
// implement this class.
abstract class ClusterNode[T <: GGMessage](serviceActor: ServiceActor[T]) extends App {
  val actorSystem: ActorSystem[GGMessage] =
    ActorSystem(GGTourBehavior.apply(serviceActor), s"ggtour-${serviceActor.serviceBaseName}-system")

  AkkaManagement(actorSystem.toClassic).start()
  ClusterBootstrap(actorSystem.toClassic).start()
}

object GGTourBehavior {
  def apply[T <: GGMessage](service: ServiceActor[T]): Behavior[GGMessage] = Behaviors.receive {
    case (context, message: T) =>
      context.spawn(service.serviceBehavior, service.getActorName) ! message
      Behaviors.same
  }
}
