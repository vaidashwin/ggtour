package io.ggtour.ladder.service

import io.ggtour.common.service.ServiceName

object LadderServiceName extends ServiceName[LadderMessage] {
  override val name: String = "ladder"
}
