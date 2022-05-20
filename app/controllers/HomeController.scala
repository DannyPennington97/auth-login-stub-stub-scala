package controllers

import javax.inject.{Inject, Singleton}
import models.ERSParams
import models.ERSParamsForm.ERSParamsForm
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.ConfigService

@Singleton
class HomeController @Inject()(val controllerComponents: MessagesControllerComponents,
                               configService: ConfigService,
                               indexView: views.html.index,
                               serviceView: views.html.service_select,
                               ersSpecialView: views.html.ers_special) extends BaseController with I18nSupport with Logging {

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

  def ersSpecial(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val maybeRedirectParams = configService.acquireConfig("ers-returns-frontend").map { config =>
      config.redirectUrl.split("\\?").tail.head.split("&").flatMap { thing => {
        thing.split("=").zipWithIndex.filter(_._2 % 2 != 0).map(_._1) //sorry
        }
      }
    }
    maybeRedirectParams.map { redirectParams =>
      val form = ERSParamsForm.fill(ERSParams(redirectParams(0), redirectParams(1), redirectParams(2), redirectParams(3), redirectParams(4), redirectParams(5)))
      Ok(ersSpecialView(form))
    }.getOrElse(InternalServerError("Who would've guessed this would break"))
  }
}
