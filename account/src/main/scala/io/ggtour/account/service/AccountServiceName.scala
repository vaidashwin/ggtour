package io.ggtour.account.service

import io.ggtour.common.service.{GGResponse, ServiceName}

object AccountServiceName extends ServiceName[AccountMessage] {
  val name = "account"

  override def replyWith[U](payload: U): GGResponse[U] with AccountMessage = AccountResponse(payload)
}
