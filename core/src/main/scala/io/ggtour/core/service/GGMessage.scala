package io.ggtour.core.service

import akka.actor.typed.ActorRef

trait GGMessage
trait GGRequest[T] extends GGMessage {
  val replyTo: Option[ActorRef[_]]
}