package io.ggtour.core.service

import java.util.UUID

import akka.actor.typed.Behavior

abstract class ServiceActor[T <: GGMessage] {
  def serviceBehavior: Behavior[T]
  def serviceBaseName: String
  def getActorName: String = s"$serviceBaseName:${UUID.randomUUID()}"
}
