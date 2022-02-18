package models

import play.api.data.Form
import play.api.data.Forms.{mapping, text}

object ServiceForm {

  val serviceForm: Form[Service] = Form(
    mapping(
      "serviceName" -> text
    )(Service.apply)(Service.unapply)
  )

}
