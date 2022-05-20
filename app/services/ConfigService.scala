package services

import javax.inject.{Inject, Singleton}
import models.ServiceConfig
import play.api.{Configuration, Logging}

import scala.concurrent.ExecutionContext
import scala.util.Try

@Singleton
class ConfigService @Inject()(implicit val config: Configuration,
                              implicit val ec: ExecutionContext) extends Logging {

  def acquireConfig(serviceName: String): Either[Throwable, ServiceConfig] = {
    Try(config.get[ServiceConfig](s"services.$serviceName")).toEither
  }

}
