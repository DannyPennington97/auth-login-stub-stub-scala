package models.auth

import play.api.libs.json.{Json, OFormat}

case class Enrolment(
                    key: String,
                    identifiers: List[Identifiers],
                    state: String = "Activated"
                    )

object Enrolment {
  implicit val formats: OFormat[Enrolment] = Json.format[Enrolment]
}