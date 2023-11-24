package models.auth

import play.api.libs.json.{Json, OFormat}

case class Identifiers(
                      key: String,
                      value: String
                      ) {

}

object Identifiers {
  implicit val formats: OFormat[Identifiers] = Json.format[Identifiers]
}