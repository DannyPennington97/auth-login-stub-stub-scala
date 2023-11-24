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

  def callALS(serviceConfig: ServiceConfig, environment: String) = {

    val alsBaseUrl = environment match {
      case "staging" => serviceConfig.stagingBaseUrl
      case "qa" => serviceConfig.qaBaseUrl
      case _ => serviceConfig.localBaseUrl
    }

    val postUrl = s"$alsBaseUrl/auth-login-stub/gg-sign-in/"

    println(s"postUrl: $postUrl")

    val request = ws.url(postUrl)
      .withHttpHeaders(("Content-Type", "application/x-www-form-urlencoded"), ("User-Agent", "ALSS"))
      .withFollowRedirects(false)

    println(s"formUrlEncodedBody is: ${serviceConfig.rawQueryString(environment)}")
    request.post(serviceConfig.rawQueryString(environment))
  }

}

/*
This worked at some point?

def callALS(serviceConfig: ServiceConfig) = {
  //val alsBaseUrl = config.get[String]("host")
  val alsBaseUrl = "https://www.staging.tax.service.gov.uk"

  val request = ws.url(s"$alsBaseUrl/auth-login-stub/gg-sign-in/")
    .withHttpHeaders(("Content-Type", "application/x-www-form-urlencoded"))
    .withFollowRedirects(false)

  println(serviceConfig.rawQueryString)
  request.post(serviceConfig.rawQueryString)
}

 */