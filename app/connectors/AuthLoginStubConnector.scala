package connectors

import javax.inject.{Inject, Singleton}
import models.ServiceConfig
import play.api.libs.ws.WSClient
import play.api.{Configuration, Logging}

import scala.concurrent.ExecutionContext

@Singleton
class AuthLoginStubConnector @Inject()(ws: WSClient)
                                      (implicit val config: Configuration,
                                       implicit val ec: ExecutionContext) extends Logging  {

  def callALS(serviceConfig: ServiceConfig) = {
    val alsBaseUrl = config.get[String]("host")

    val request = ws.url(s"$alsBaseUrl/auth-login-stub/gg-sign-in/")
      .withHttpHeaders(("Content-Type", "application/x-www-form-urlencoded"))
      .withFollowRedirects(false)

    //println(serviceConfig.rawQueryString)
    request.post(serviceConfig.rawQueryString)
  }

}
