package io.ggtour.common.service

trait GGMessage
trait GGRequest[T] extends GGMessage {
  val replyTo: (ServiceName[_], String)
}

class GGResponse[T](val payload: T)
object GGResponse {
  def unapply[T](response: GGResponse[T]): Option[T] = Some(response.payload)
}