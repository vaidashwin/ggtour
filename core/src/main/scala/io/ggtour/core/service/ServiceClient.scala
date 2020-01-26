package io.ggtour.core.service

import akka.actor.typed.Scheduler
import akka.util.Timeout

import scala.concurrent.Future

// Abstracts out actor lookup logic for inter- and intra-service messaging.
object ServiceClient {
  private var clientImpl: Option[ServiceClientImpl] = None
  private def getClientImpl =
    clientImpl.getOrElse(throw new RuntimeException("ServiceClient has not been initialized."))
  def init(clientImpl: ServiceClientImpl): Unit = {
    this.clientImpl = Some(clientImpl)
  }

  def ! (message: GGMessage): Unit = getClientImpl ! message
  def ?[T] (message: GGRequest[T])(implicit timeout: Timeout, scheduler: Scheduler): Future[T] =
    getClientImpl ? message

}

trait ServiceClientImpl {
  def ! (message: GGMessage): Unit
  def ?[T] (message: GGRequest[T])(implicit timeout: Timeout, scheduler: Scheduler): Future[T]
}
