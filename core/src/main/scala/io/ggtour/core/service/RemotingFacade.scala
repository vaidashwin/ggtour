package io.ggtour.core.service

import akka.actor.ActorPath
import akka.actor.typed.{ActorRef, ActorRefResolver, ActorSystem}
import akka.actor.typed.scaladsl.ActorContext
import io.ggtour.common.service.GGResponse.ServiceAndPath
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
  implicit def convertMyActor2Path[T <: GGMessage](actorRef: ActorRef[T])(
      implicit system: ActorSystem[_],
      serviceName: ServiceName[T]): ServiceAndPath =
    (
      serviceName,
      ActorRefResolver(system).toSerializationFormat(actorRef)
    )
}

trait ActorRefProvider {
  def getActorForPath[T](
      actorPath: String,
      targetService: ServiceName[GGMessage]): ActorRef[T]
  def getServiceActor[T <: GGMessage](
      serviceName: ServiceName[T],
      actorSystem: ActorSystem[_]): ActorRef[T]
}
