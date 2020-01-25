package io.ggtour.core.service

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement

// Shared logic for entry point for each node in the cluster. Each service should
// implement this class.
abstract class ClusterNode(serviceActor: ServiceActor) extends App {
  val actorSystem: ActorSystem[GGMessage] =
    ActorSystem(GGTourBehavior.apply(serviceActor), "ggtour-system")

  AkkaManagement(actorSystem).start()
  ClusterBootstrap(actorSystem).start()
}

object GGTourBehavior {
  def apply(service: ServiceActor): Behavior[GGMessage] = Behaviors.receive {
    case (context, message: GGMessage) =>
      context.spawn(service.serviceBehavior, service.getActorName) ! message
      Behaviors.same
  }
}
