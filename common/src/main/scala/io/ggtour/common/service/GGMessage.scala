package io.ggtour.common.service

import io.ggtour.common.service.GGResponse.ServiceAndPath

trait GGMessage
trait GGRequest[T] extends GGMessage {
  val replyTo: ServiceAndPath
}

class GGResponse[T](val payload: T)
object GGResponse {
  type ServiceAndPath = (ServiceName[_ <: GGMessage], String)
  def unapply[T](response: GGResponse[T]): Option[T] = Some(response.payload)
}