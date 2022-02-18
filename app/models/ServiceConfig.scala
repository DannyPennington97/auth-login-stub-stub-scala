package models

import com.typesafe.config.Config
import play.api.ConfigLoader

import scala.util.{Failure, Success, Try}

case class ServiceConfig(
                          redirectUrl: String,
                          credentialStrength: String = "Strong",
                          confidenceLevel: String = "50",
                          affinityGroup: String = "Individual",
                          enrolmentKey: Option[String] = None,
                          enrolmentIdentifierName0: Option[String] = None,
                          enrolmentIdentifierName1: Option[String] = None,
                          enrolmentsIdentifierValue0: Option[String] = None,
                          enrolmentsIdentifierValue1: Option[String] = None
                        ) {

  def toQueryParams: List[(String, String)] = {
    List(
      ("redirectionUrl", redirectUrl),
      ("credentialStrength", credentialStrength),
      ("confidenceLevel", confidenceLevel),
      ("affinityGroup", affinityGroup),
      ("enrolment[0].name", affinityGroup),
      ("enrolment[0].taxIdentifier[0].name", enrolmentIdentifierName0.getOrElse("")),
      ("enrolment[0].taxIdentifier[0].value", enrolmentsIdentifierValue0.getOrElse("")),
      ("enrolment[0].taxIdentifier[1].name", enrolmentIdentifierName1.getOrElse("")),
      ("enrolment[0].taxIdentifier[1].value", enrolmentsIdentifierValue1.getOrElse(""))
      )
  }
}

object ServiceConfig {

  implicit val configLoader: ConfigLoader[ServiceConfig] = (rootConfig: Config, path: String) => {

    val config = rootConfig.getConfig(path)
    ServiceConfig(
      redirectUrl = config.getString("redirectUrl"),
      credentialStrength = config.getString("credentialStrength"),
      confidenceLevel = config.getString("confidenceLevel"),
      affinityGroup = config.getString("affinityGroup"),
      enrolmentKey = config.getOptional("enrolment.key"),
      enrolmentIdentifierName0 = config.getOptional("enrolment.identifier0.name"),
      enrolmentIdentifierName1 = config.getOptional("enrolment.identifier1.name"),
      enrolmentsIdentifierValue0 = config.getOptional("enrolment.identifier0.value"),
      enrolmentsIdentifierValue1 = config.getOptional("enrolment.identifier1.value")

    )
  }

  implicit class CoolConfig(config: Config) {

    def getOptional(key: String): Option[String] = {
      Try(config.getString(key)) match {
        case Success(value) => Some(value)
        case Failure(_) => None
      }
    }
  }
}

