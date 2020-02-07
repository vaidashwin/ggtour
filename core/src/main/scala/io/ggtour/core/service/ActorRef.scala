package io.ggtour.core.service

import akka.actor.ActorPath
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.ActorContext
import io.ggtour.common.service.{GGMessage, ServiceName}

object RemotingFacade {
  // replace with Kafka impl as the default
  private var provider: Option[ActorRefProvider] = None
  private def getProvider =
    provider.getOrElse(
      throw new RuntimeException("ActorRef provider not configured."))
  def getActorForPath[T](actorPath: String, targetService: ServiceName[GGMessage]): ActorRef[T] =
    getProvider.getActorForPath(actorPath, targetService)
  def getServiceActor[T <: GGMessage](serviceName: ServiceName[T])(
      implicit actorSystem: ActorSystem[_]): ActorRef[T] =
    getProvider.getServiceActor(serviceName, actorSystem)
  def provideWith(provider: ActorRefProvider): Unit =
    this.provider = Some(provider)
}

trait ActorRefProvider {
  def getActorForPath[T](
      actorPath: String,
      targetService: ServiceName[GGMessage]): ActorRef[T]
  def getServiceActor[T <: GGMessage](
      serviceName: ServiceName[T],
      actorSystem: ActorSystem[_]): ActorRef[T]
}
