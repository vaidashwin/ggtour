package io.ggtour.ladder.json

import io.ggtour.common.json.CommonJsonProtocols
import io.ggtour.ladder.challenge.Challenge
import io.ggtour.ladder.elo.{GameResult, Result}
import io.ggtour.ladder.elo.Result.Result
import spray.json.{JsString, JsValue, JsonFormat, RootJsonFormat}

object LadderJsonFormats extends CommonJsonProtocols {
  implicit val challengeFormat: JsonFormat[Challenge] = jsonFormat7(Challenge)
  implicit val gameResultFormat: JsonFormat[GameResult] = jsonFormat1(GameResult)
  implicit val resultFormat: JsonFormat[Result.Value] = new JsonFormat[Result.Value] {
    override def read(json: JsValue): Result.Value = json match {
      case JsString("Win") => Result.Win
      case JsString("Loss") => Result.Loss
      case _ => throw new RuntimeException("Invalid game result in JSON deserialization.")
    }

    override def write(obj: Result): JsValue = JsString(obj.toString)
  }

}
