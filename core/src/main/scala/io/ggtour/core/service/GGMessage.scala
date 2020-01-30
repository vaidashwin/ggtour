package io.ggtour.core.service

import akka.actor.typed.ActorRef

trait GGMessage {
  val service: String
}
trait GGRequest[T] extends GGMessage {
  val replyTo: Option[ActorRef[_]]
}