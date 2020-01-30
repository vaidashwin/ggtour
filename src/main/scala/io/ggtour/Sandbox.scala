package main.scala.io.ggtour

import io.ggtour.core.service.{GGMessage, ServiceClient, ServiceNode}
import io.ggtour.discord.DiscordService
import io.ggtour.ladder.service.LadderService

object Sandbox extends App {
  // Add handlers here for any new services for sandbox testing.
  val services: Map[String, ServiceNode[_]] = Map(
    "discord" -> DiscordService,
    "ladder" -> LadderService
  )
  // Kick off the services.
  services.map(_._2.main(args))

  ServiceClient.init(new SandboxServiceClient(services))
}
