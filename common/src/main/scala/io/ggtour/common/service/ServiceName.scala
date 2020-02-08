package io.ggtour.common.service

trait ServiceName[+T <: GGMessage] {
  val name: String
  override def toString: String = name
  def replyWith[U](any: U): GGResponse[U] with T
}
