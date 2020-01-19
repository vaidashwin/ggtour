package io.ggtour.core.service

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement

object Clustering {
  private var system: Option[ActorSystem[GGMessage]] = None

  def actorSystem: ActorSystem[GGMessage] =
    system.getOrElse(
      throw new RuntimeException("Clustering was not initialized."))
  // bootstrap actor system and clustering
  def init(serviceActor: ServiceActor): Unit =
    if (system.isEmpty) {
      system = Some(
        ActorSystem(GGTourBehavior.apply(serviceActor), "ggtour-system"))
      AkkaManagement(actorSystem).start()
      ClusterBootstrap(actorSystem).start()
    } else {
      system.foreach(
        _.log.warn("Attempted to initialize Clustering more than once."))
    }
}

object GGTourBehavior {
  def apply(service: ServiceActor): Behavior[GGMessage] = Behaviors.receive {
    case (context, message: GGMessage) =>
      context.spawn(service.serviceBehavior, service.getActorName) ! message
      Behaviors.same
  }
}
