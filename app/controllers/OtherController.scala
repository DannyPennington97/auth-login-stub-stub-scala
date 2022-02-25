package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Cookie}
import models.{ServiceConfig, ServiceForm}
import play.api.libs.ws.{WSClient, WSCookie}
import play.api.{Configuration, Logging}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OtherController @Inject()(val controllerComponents: ControllerComponents,
                                ws: WSClient)
                               (implicit val config: Configuration,
                                implicit val ec: ExecutionContext) extends BaseController with Logging {

  def quickLogin(): Action[AnyContent] = Action.async { implicit request =>
    ServiceForm.serviceForm.bindFromRequest().fold(
      _ => {
        logger.warn("User didn't pick a service somehow???")
        Future.successful(BadRequest("How did you mess that up? It's literally a drop down selection. Shame on you (or me idk)"))
      },
      form => {
        logger.debug(s"User chose: $form.name")
        val config = acquireConfig(form.name)
        val request = ws.url("http://127.0.0.1:9949/auth-login-stub/gg-sign-in/")
          .withHttpHeaders(("Content-Type", "application/x-www-form-urlencoded"))
          .withFollowRedirects(false)
        request.post(config.rawQueryString).map { response =>
          Redirect(response.header("Location").get).withCookies(response.cookies.toSeq.map(_.toPlayCookie): _*)
        }
      }
    )
  }

  def acquireConfig(serviceName: String): ServiceConfig = {
    config.get[ServiceConfig](s"services.$serviceName")
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
