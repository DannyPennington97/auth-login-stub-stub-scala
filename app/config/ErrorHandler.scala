package config

import javax.inject.{Inject, Singleton}
import play.api.Logging
import play.api.http.HttpErrorHandler
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.InternalServerError
import play.api.mvc.{AnyContent, Request, RequestHeader, Result}

import scala.concurrent.Future
import scala.language.implicitConversions


@Singleton
class ErrorHandler @Inject ()(val messagesApi: MessagesApi,
                              errorView: views.html.error) extends HttpErrorHandler with Logging with I18nSupport {

  private implicit def rhToRequest(rh: RequestHeader): Request[_] = Request(rh, "")

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    if (!message.contains("Resource not found by Assets controller")){
      logger.error(s"Something went wrong here m8. Status: $statusCode, Message: $message")
    }
    Future.successful(InternalServerError("Oh no"))

  }
  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    logger.error(s"Something went wrong here m8. Exception: $exception")
    Future.successful(InternalServerError(
      errorView(exception.getMessage, exception.getStackTrace.mkString("\n"))(request2Messages(request), Request(request, AnyContent("")))
    ))
  }

}
