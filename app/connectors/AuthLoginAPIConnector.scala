package connectors

import javax.inject.{Inject, Singleton}
import models.ServiceConfig
import models.auth.AuthLoginAPIPayload
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.{Configuration, Logging}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthLoginAPIConnector @Inject()(ws: WSClient)
                                      (implicit val config: Configuration,
                                       implicit val ec: ExecutionContext) extends Logging  {

  def callALA(serviceConfig: ServiceConfig, environment: String, payload: AuthLoginAPIPayload): Future[String] = {

    val alAPIBaseUrl = environment match {
      case "staging" => serviceConfig.stagingBaseUrl
      case "qa" => serviceConfig.qaBaseUrl
      case _ => "http://localhost:8585"
    }

    val postUrl = s"$alAPIBaseUrl/government-gateway/session/login"
    val body = Json.toJson(payload)
    println(s"postUrl: $postUrl")
    println("Body: " + body)

    val request = ws.url(postUrl).withHttpHeaders(("Content-Type", "application/json"), ("User-Agent", "ALSS"))

    request.post(body).map {res =>
      println(s"Headers: ${res.headers.mkString("\n\n", "\n", "\n")}\nBody: ${res.body}")
      res.header("Authorization").get
    }
  }

}
