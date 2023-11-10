package models

import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.libs.json.{Json, OFormat}

case class TrustsParams(agentOrOrg: String, taxableOrNonTaxable: String, UTRorURN: String) {

}


case object TrustsParams {
  implicit val format: OFormat[TrustsParams] = Json.format[TrustsParams]
}

object TrustsParamsForm {

  val TrustsParamsForm: Form[TrustsParams] = Form(
    mapping(
      "agentOrOrg"          -> nonEmptyText,
      "taxableOrNonTaxable" -> nonEmptyText,
      "UTRorURN"            -> nonEmptyText
    )(TrustsParams.apply)(TrustsParams.unapply)
  )
}
