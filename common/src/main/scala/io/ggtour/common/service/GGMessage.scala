package io.ggtour.common.service

trait GGMessage {
  val service: String
}
trait GGRequest[T] extends GGMessage {
  val replyTo: (ServiceName[_], String)
}