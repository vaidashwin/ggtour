package io.ggtour.ladder.service

import io.ggtour.common.service.{GGResponse, ServiceName}

object LadderServiceName extends ServiceName[LadderMessage] {
  override val name: String = "ladder"

  override def replyWith[U](payload: U): GGResponse[U] with LadderMessage = LadderResponse(payload)
}
