package controllers

import connectors.AuthLoginStubConnector
import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Cookie}
import models.ServiceForm
import play.api.i18n.I18nSupport
import play.api.libs.ws.WSCookie
import play.api.{Configuration, Logging}
import services.AuthLoginStubService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ALSController @Inject()(val controllerComponents: ControllerComponents,
                              alsService: AuthLoginStubService,
                              alsConnector: AuthLoginStubConnector,
                              errorView: views.html.error)
                             (implicit val config: Configuration,
                                implicit val ec: ExecutionContext) extends BaseController with Logging with I18nSupport  {

  def quickLogin(): Action[AnyContent] = Action.async { implicit request =>
    ServiceForm.serviceForm.bindFromRequest().fold(
      _ => {
        logger.warn("User didn't pick a service somehow???")
        Future.successful(Redirect(routes.HomeController.index()).flashing(("error" -> "error.badform")))
      },
      form => {
        logger.debug(s"User chose: $form.name")
        alsService.acquireConfig(form.name).fold(ex => {
          logger.error(s"Invalid config key provided. Message is: ${ex.getMessage}")
          Future(InternalServerError(errorView(ex.getMessage)))
        },
        serviceConfig =>
          alsConnector.callALS(serviceConfig).map { response =>
            Redirect(response.header("Location").get).withCookies(response.cookies.toSeq.map(_.toPlayCookie): _*)
          })
      }
    )
  }

  def customLogin(): Action[AnyContent] = Action { implicit request =>
    NotImplemented("Yeah this isn't ready yet sorry")
  }


  implicit class CoolCookie(cookie: WSCookie) {
    def toPlayCookie: Cookie = {
      Cookie(cookie.name, cookie.value, cookie.maxAge.map(_.toInt), cookie.path.getOrElse(""), cookie.domain)
    }
  }

}
