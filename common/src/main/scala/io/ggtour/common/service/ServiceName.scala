package io.ggtour.common.service

trait ServiceName[+T <: GGMessage] {
  val name: String
  override def toString: String = name
}
