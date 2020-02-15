package io.ggtour

import akka.actor.ActorPath
import akka.actor.typed.{ActorRef, ActorRefResolver, ActorSystem}
import io.ggtour.account.service.{AccountService, AccountServiceName}
import io.ggtour.common.service.{GGMessage, ServiceName}
import io.ggtour.core.service.{ActorRefProvider, RemotingFacade, ServiceNode}
import io.ggtour.discord.DiscordService
import io.ggtour.discord.service.DiscordServiceName
import io.ggtour.ladder.service.{LadderService, LadderServiceName}

object Sandbox extends App {
  // Add handlers here for any new services for sandbox testing.
  val services: List[ServiceNode[_ <: GGMessage]] =
    AccountService ::
      DiscordService ::
      LadderService ::
      Nil
  RemotingFacade.provideWith(SandboxActorRefProvider)

  // Kick off the services.
  services.map(_.main(args))

}

object SandboxActorRefProvider extends ActorRefProvider {
  private def getServiceSystemByName[T <: GGMessage](serviceName: ServiceName[T]): ActorSystem[T] = {
    serviceName match {
      case AccountServiceName => AccountService.system()
      case DiscordServiceName => DiscordService.system()
      case LadderServiceName  => LadderService.system()
    }
  }
  override def getActorForPath[T](
      actorPath: String,
      serviceName: ServiceName[GGMessage]): ActorRef[T] = {
    val targetSystem = getServiceSystemByName(serviceName)
    ActorRefResolver(targetSystem).resolveActorRef(actorPath)
  }

  override def getServiceActor[T <: GGMessage](
      serviceName: ServiceName[T],
      actorSystem: ActorSystem[_]): ActorRef[T] = getServiceSystemByName(serviceName)
}
