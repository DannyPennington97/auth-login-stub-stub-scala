package models.auth

import play.api.libs.json.{Json, OFormat}

case class AuthLoginAPIPayload(
                              credId: String,
                              affinityGroup: String,
                              confidenceLevel: Int,
                              credentialStrength: String,
                              enrolments: Option[List[Enrolment]],
                              excludeGNAPtoken: Boolean = true
                              ) {

}

object AuthLoginAPIPayload {
  implicit val formats: OFormat[AuthLoginAPIPayload] = Json.format[AuthLoginAPIPayload]
}
