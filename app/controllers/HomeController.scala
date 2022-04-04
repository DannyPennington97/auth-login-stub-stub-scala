package controllers

import javax.inject.{Inject, Singleton}
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc._

@Singleton
class HomeController @Inject()(val controllerComponents: MessagesControllerComponents,
                               indexView: views.html.index,
                               serviceView: views.html.serviceSelect) extends BaseController with I18nSupport with Logging {

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val services = Array(
      "ers-returns-frontend",
      "ers-checking-frontend",
      "request-corporation-tax-number-frontend",
      "ras-frontend"
    )
    Ok(indexView(services))
  }

  def newLayout(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val services = Array(
      "ers-returns-frontend",
      "ers-checking-frontend",
      "request-corporation-tax-number-frontend",
      "ras-frontend"
    )
    Ok(serviceView(services))
  }
}
