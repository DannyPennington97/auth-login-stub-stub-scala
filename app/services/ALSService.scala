package services

import play.api.{Configuration, Logging}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ALSService @Inject()(implicit val config: Configuration,
                           implicit val ec: ExecutionContext) extends Logging {


}
