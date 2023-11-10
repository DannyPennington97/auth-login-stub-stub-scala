package models

import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, text}
import play.api.libs.json.{Json, OFormat}

case class ERSParams(aoRef: String,
                     taxYear: String,
                     ersSchemeRef: String,
                     schemeType: String,
                     schemeName: String,
                     hmac: String) {

  def toRedirectUrl: String =
    s"/submit-your-ers-annual-return?aoRef=$aoRef&taxYear=$taxYear&ersSchemeRef=$ersSchemeRef&schemeType=$schemeType&schemeName=$schemeName&hmac=$hmac"
}

case object ERSParams {
  implicit val format: OFormat[ERSParams] = Json.format[ERSParams]
}

object ERSParamsForm {

  val ERSParamsForm: Form[ERSParams] = Form(
    mapping(
      "aoRef"        -> nonEmptyText,
      "taxYear"      -> text,
      "ersSchemeRef" -> text,
      "schemeType"   -> text,
      "schemeName"   -> text,
      "hmac"         -> text
    )(ERSParams.apply)(ERSParams.unapply)

  )
}