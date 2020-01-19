package io.ggtour.common.json

import java.util.UUID

import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat}

abstract class CommonJsonProtocols extends DefaultJsonProtocol {
  implicit val uuidFormat = new JsonFormat[UUID] {
    override def read(json: JsValue): UUID = json match {
      case JsString(value) => UUID.fromString(value)
      case _               => throw new RuntimeException("Bad UUID format.")
    }

    override def write(obj: UUID): JsValue = JsString(obj.toString)
  }

}
