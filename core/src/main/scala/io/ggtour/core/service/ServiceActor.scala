package io.ggtour.core.service

import java.util.UUID

import akka.actor.typed.Behavior
import io.ggtour.common.service.{GGMessage, ServiceName}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

abstract class ServiceActor[T <: GGMessage](val serviceBaseName: ServiceName[T]) {
  val logger = LoggerFactory.getLogger(this.getClass)
  def serviceBehavior: Behavior[T]
  def getActorName: String = s"$serviceBaseName:${UUID.randomUUID()}"
}
