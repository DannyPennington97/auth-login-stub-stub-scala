package controllers

import javax.inject.{Inject, Singleton}
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc._

@Singleton
class HomeController @Inject()(val controllerComponents: MessagesControllerComponents,
                               indexView: views.html.index) extends BaseController with I18nSupport with Logging {

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val services = Array("ers-returns-frontend", "ers-checking-frontend")
    Ok(indexView(services))
  }
}
