package io.ggtour.core.service

import java.util.UUID

import akka.actor.typed.Behavior
import io.ggtour.common.service.{GGMessage, ServiceName}
import ch.qos.logback.core.rolling.RollingFileAppender
import io.ggtour.common.logging.LazyLogging

abstract class ServiceActor[T <: GGMessage](serviceBaseName: ServiceName[T]) extends LazyLogging {
  implicit final val serviceName: ServiceName[T] = serviceBaseName
  def serviceBehavior: Behavior[T]
  def getActorName: String = s"$serviceBaseName:${UUID.randomUUID()}"
}
