package io.ggtour.ladder.elo

import java.util.UUID

case class GameResult(results: Map[UUID, Result.Value])

object Result extends Enumeration {
  type Result = Value
  val Win, Loss = Value
}
