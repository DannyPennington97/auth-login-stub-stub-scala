package controllers

import connectors.AuthLoginStubConnector

import javax.inject.{Inject, Singleton}
import models.ERSParamsForm.ERSParamsForm
import models.TrustsParamsForm.TrustsParamsForm
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Cookie, Request, Result}
import models.{ServiceForm, TrustsParams}
import play.api.i18n.I18nSupport
import play.api.libs.ws.WSCookie
import play.api.{Configuration, Logging}
import services.ConfigService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ALSController @Inject()(val controllerComponents: ControllerComponents,
                              configService: ConfigService,
                              alsConnector: AuthLoginStubConnector,
                              errorView: views.html.error,
                              ersSpecialView: views.html.ers_special,
                              trustsSpecialView: views.html.trusts)
                             (implicit val config: Configuration,
                                implicit val ec: ExecutionContext) extends BaseController with Logging with I18nSupport  {

  private def getConfigAndRedirect(service: String)(implicit request: Request[AnyContent]): Future[Result] = {
    configService.acquireConfig(service).fold(ex => {
      logger.error(s"Invalid config key provided. Message is: ${ex.getMessage}")
      Future(InternalServerError(errorView(ex.getMessage, ex.getStackTrace.mkString("\n"))))
    },
      serviceConfig =>
        alsConnector.callALS(serviceConfig).map { response =>
          //logger.debug(s"Response from ALS is: ${response.body}")
          Redirect(response.header("Location").get).withCookies(response.cookies.toSeq.map(_.toPlayCookie): _*)
        }.recover { ex =>
          logger.error("Call to ALS failed, is it definitely running???")
          InternalServerError(errorView(ex.getMessage, ex.getStackTrace.mkString("\n")))
        })
  }

  def quickLogin(): Action[AnyContent] = Action.async { implicit request =>
    ServiceForm.serviceForm.bindFromRequest().fold(
      _ => {
        logger.warn("User didn't pick a service somehow???")
        Future.successful(Redirect(routes.HomeController.index()).flashing(("error" -> "error.badform")))
      },
      form => {
        logger.debug(s"User chose: ${form.name}")
        getConfigAndRedirect(form.name)
      }
    )
  }

  def customLogin(): Action[AnyContent] = Action { implicit request =>
    NotImplemented("Yeah this isn't ready yet sorry")
  }

  def quickUrlLogin(service: String): Action[AnyContent] = Action.async { implicit request =>
    getConfigAndRedirect(service)
  }

  def customUrlLogin(service: String): Action[AnyContent] = Action { implicit request =>
    NotImplemented("Yeah this isn't ready yet either sorry")
  }

  def handleErsSpecial(): Action[AnyContent] = Action.async { implicit request =>
    ERSParamsForm.bindFromRequest.fold(
      formWithErrors => {
        Future(BadRequest(ersSpecialView(formWithErrors)))
      },
      ersParams => {
        configService.acquireConfig("ers-returns-frontend").fold(ex => {
          logger.error(s"Invalid config key provided. Message is: ${ex.getMessage}")
          Future(InternalServerError(errorView(ex.getMessage, ex.getStackTrace.mkString("\n"))))
        },
          serviceConfig => {
            val newConfig = serviceConfig.copy(redirectUrl = ersParams.toRedirectUrl)
            alsConnector.callALS(newConfig).map { response =>
              logger.debug(s"Response from ALS is: ${response.body}")
              Redirect(response.header("Location").get).withCookies(response.cookies.toSeq.map(_.toPlayCookie): _*)
            }
          }
        )
      }
    )
  }

  def handleTrustsSpecial(): Action[AnyContent] = Action.async { implicit request =>
    TrustsParamsForm.bindFromRequest.fold(
      formWithErrors => {
        Future(BadRequest(trustsSpecialView(formWithErrors)))
      },
      trustsParams => {
        configService.acquireConfig("trusts").fold(ex => {
          logger.error(s"Invalid config key provided. Message is: ${ex.getMessage}")
          Future(InternalServerError(errorView(ex.getMessage, ex.getStackTrace.mkString("\n"))))
        },
          serviceConfig => {
            val newConfig = trustsParams.agentOrOrg match {
              case "agent" =>
                serviceConfig.copy(
                  affinityGroup = "Agent",
                  enrolmentKey = Some("HMRC-AS-AGENT"),
                  enrolmentIdentifierName0 = Some("AgentReferenceNumber"),
                  enrolmentsIdentifierValue0 = Some("SARN1234567"),
                  delegatedEnrolmentKey = if (trustsParams.taxableOrNonTaxable == "taxable") {Some("HMRC-TERS-ORG")} else {Some("HMRC-TERSNT-ORG")},
                  delegatedEnrolmentName0 = if (trustsParams.taxableOrNonTaxable == "taxable") {Some("SAUTR")} else {Some("URN")},
                  delegatedEnrolmentValue0 = Some(trustsParams.UTRorURN)
                )
              case "org" =>
                serviceConfig.copy(
                  enrolmentKey = if (trustsParams.taxableOrNonTaxable == "taxable") {Some("HMRC-TERS-ORG")} else {Some("HMRC-TERSNT-ORG")},
                  enrolmentIdentifierName0 = if (trustsParams.taxableOrNonTaxable == "taxable") {Some("SAUTR")} else {Some("URN")},
                  enrolmentsIdentifierValue0 = Some(trustsParams.UTRorURN)
                )
            }
            alsConnector.callALS(newConfig).map { response =>
              logger.debug(s"Response from ALS is: ${response.body}")
              Redirect(response.header("Location").get).withCookies(response.cookies.toSeq.map(_.toPlayCookie): _*)
            }
          }
        )
      }
    )
  }

  implicit class CoolCookie(cookie: WSCookie) {
    def toPlayCookie: Cookie = {
      Cookie(cookie.name, cookie.value, cookie.maxAge.map(_.toInt), cookie.path.getOrElse(""), cookie.domain)
    }
  }

}
