package services

import connectors.AuthLoginAPIConnector

import play.api.mvc.Session
import play.api.{Configuration, Logging}

import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.ExecutionContext


class SessionService @Inject()(authLoginAPIConnector: AuthLoginAPIConnector)
                              (implicit val config: Configuration,
                           implicit val ec: ExecutionContext) extends Logging {


  def buildGGSession(authToken: String): Session = {
    println("Authtoken: " + authToken.split(",").filterNot(_.startsWith("G")).head)
    Session(
    Map(
      "sessionId" -> s"session-${UUID.randomUUID}",
      "authToken" -> authToken.split(",").filterNot(_.startsWith("G")).head,
      "ts" -> Instant.now.toEpochMilli.toString
    )
  )
  }

}
