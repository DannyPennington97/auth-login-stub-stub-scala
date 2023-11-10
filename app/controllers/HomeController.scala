package controllers

import javax.inject.{Inject, Singleton}
import models.{ERSParams, TrustsParams}
import models.ERSParamsForm.ERSParamsForm
import models.TrustsParamsForm.TrustsParamsForm
import play.api.{Configuration, Logging}
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.ConfigService

@Singleton
class HomeController @Inject()(val controllerComponents: MessagesControllerComponents,
                               config: Configuration,
                               configService: ConfigService,
                               indexView: views.html.index,
                               serviceView: views.html.service_select,
                               ersSpecialView: views.html.ers_special,
                               trustsSpecialView: views.html.trusts) extends BaseController with I18nSupport with Logging {

  val services: Seq[String] = config.get[Seq[String]]("supportedServices")

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(indexView(services))
  }

  def newLayout(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
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

  def trustsSpecial(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(trustsSpecialView(TrustsParamsForm.fill(TrustsParams("org", "taxable", "1000000001"))))

  }
}
