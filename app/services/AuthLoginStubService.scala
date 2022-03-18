package services

import javax.inject.{Inject, Singleton}
import models.ServiceConfig
import play.api.{Configuration, Logging}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

@Singleton
class AuthLoginStubService @Inject()(implicit val config: Configuration,
                                     implicit val ec: ExecutionContext) extends Logging {

  def acquireConfig(serviceName: String): Either[Throwable, ServiceConfig] = {
    Try(config.get[ServiceConfig](s"services.$serviceName")) match {
      case Success(config) => Right(config)
      case Failure(error) => Left(error)
    }
  }

}
