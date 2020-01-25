package io.ggtour.core.service

import java.util.UUID

import akka.actor.typed.Behavior

abstract class ServiceActor {
  def serviceBehavior: Behavior[GGMessage]
  protected def serviceBaseName: String
  def getActorName: String = s"$serviceBaseName:${UUID.randomUUID()}"
}
