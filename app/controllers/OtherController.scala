package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import models.{ServiceConfig, ServiceForm}
import play.api.libs.ws.WSClient
import play.api.{Configuration, Logging}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OtherController @Inject()(val controllerComponents: ControllerComponents,
                                ws: WSClient)
                               (implicit val config: Configuration,
                                implicit val ec: ExecutionContext) extends BaseController with Logging {

  def quickLogin(): Action[AnyContent] = Action.async { implicit request =>
    logger.warn(s"Request body: ${request.body}")
    ServiceForm.serviceForm.bindFromRequest().fold(
      _ => {
        logger.warn("User didn't pick a service somehow???")
        Future.successful(BadRequest("How did you mess that up? It's literally a drop down selection. Shame on you (or me idk)"))
      },
      form => {
        logger.debug(s"User chose: $form.name")
        val config = acquireConfig(form.name)
        val request = ws.url("http://localhost:9949/auth-login-stub/gg-sign-in/").withQueryStringParameters(config.toQueryParams: _*)
        logger.error(request.uri.toString)
        request.post("").map { response =>
          logger.error(s"Status: ${response.status}, headers: ${response.headers.toString()}")
          //Redirect(response.header("Location").getOrElse("https://www.google.com"))
          Ok(response.body)
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

}
