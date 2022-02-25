package config

import play.api.Logging
import play.api.http.HttpErrorHandler
import play.api.mvc.Results.InternalServerError
import play.api.mvc.{RequestHeader, Result}

import scala.concurrent.Future

class ErrorHandler extends HttpErrorHandler with Logging {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    logger.error(s"Something went wrong here m8. Status: $statusCode, Message: $message")
    Future.successful(InternalServerError("Oh no"))

  }
  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    logger.error(s"Something went wrong here m8. Exception: $exception")
    Future.successful(InternalServerError(exception.getMessage + "\n" + exception.getStackTrace.mkString("/n")))
  }

}