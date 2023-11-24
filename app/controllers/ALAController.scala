package controllers

import connectors.AuthLoginAPIConnector
import models.ServiceConfig
import models.auth.AuthLoginAPIPayload
import play.api.{Configuration, Logging}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request, Result}
import services.{ConfigService, SessionService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ALAController @Inject()(val controllerComponents: ControllerComponents,
                              configService: ConfigService,
                              sessionService: SessionService,
                              alaConnector: AuthLoginAPIConnector)
                             (implicit val config: Configuration,
                              implicit val ec: ExecutionContext) extends BaseController with Logging with I18nSupport {

  def getTokenAndShow(): Action[AnyContent] = Action.async { implicit request =>

    val environment = "local"
    configService.acquireConfig("ers-checking-frontend") match {
      case Right(serviceConfig) =>
        val payload = serviceConfig.toALAPayload()
        println("Payload:\n" + payload)
        alaConnector.callALA(serviceConfig, environment, payload).map { authHeader =>
          Redirect(serviceConfig.localhost + serviceConfig.redirectUrl).withSession(sessionService.buildGGSession(authHeader))
        }
      case Left(_) => Future.successful(InternalServerError("Awwww man"))
    }
  }
}