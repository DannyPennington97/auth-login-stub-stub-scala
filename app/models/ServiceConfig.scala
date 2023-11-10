package models

import java.net.URLEncoder

import com.typesafe.config.Config
import play.api.ConfigLoader

import scala.util.{Failure, Success, Try}

case class ServiceConfig(
                          localport: String,
                          redirectUrl: String,
                          credentialStrength: String,
                          confidenceLevel: String,
                          affinityGroup: String,
                          enrolmentKey: Option[String] = None,
                          enrolmentIdentifierName0: Option[String] = None,
                          enrolmentIdentifierName1: Option[String] = None,
                          enrolmentsIdentifierValue0: Option[String] = None,
                          enrolmentsIdentifierValue1: Option[String] = None,
                          delegatedEnrolmentKey: Option[String] = None,
                          delegatedEnrolmentName0: Option[String] = None,
                          delegatedEnrolmentValue0: Option[String] = None,
                        ) {

  val enrolmentState = "Activated"
  val authorityId = "1"
  val localhost = s"http://localhost:$localport"
  val runLocal = true

  def toQueryParams: List[(String, String)] = {
    List(
      ("redirectionUrl", redirectUrl),
      ("credentialStrength", credentialStrength),
      ("confidenceLevel", confidenceLevel),
      ("affinityGroup", affinityGroup),
      ("authorityId", "1"),
      ("enrolment[0].name", enrolmentKey.getOrElse("")),
      ("enrolment[0].taxIdentifier[0].name", enrolmentIdentifierName0.getOrElse("")),
      ("enrolment[0].taxIdentifier[0].value", enrolmentsIdentifierValue0.getOrElse("")),
      ("enrolment[0].taxIdentifier[1].name", enrolmentIdentifierName1.getOrElse("")),
      ("enrolment[0].taxIdentifier[1].value", enrolmentsIdentifierValue1.getOrElse("")),
      ("delegatedEnrolment[0].key", delegatedEnrolmentKey.getOrElse("")),
      ("delegatedEnrolment[0].taxIdentifier[0].name", delegatedEnrolmentName0.getOrElse("")),
      ("delegatedEnrolment[0].taxIdentifier[0].value", delegatedEnrolmentValue0.getOrElse("")),
      )
  }

  def rawQueryString: String = {
    val updatedUrl = URLEncoder.encode(if (runLocal) localhost + redirectUrl else redirectUrl)

    s"redirectionUrl=$updatedUrl&credentialStrength=$credentialStrength&confidenceLevel=$confidenceLevel&affinityGroup=$affinityGroup" +
      s"&enrolment[0].name=${enrolmentKey.getOrElse("")}&enrolment[0].state=$enrolmentState" +
      s"&enrolment[0].taxIdentifier[0].name=${enrolmentIdentifierName0.getOrElse("")}&enrolment[0].taxIdentifier[0].value=${enrolmentsIdentifierValue0.getOrElse("")}" +
      s"&enrolment[0].taxIdentifier[1].name=${enrolmentIdentifierName1.getOrElse("")}&enrolment[0].taxIdentifier[1].value=${enrolmentsIdentifierValue1.getOrElse("")}" +
      s"&delegatedEnrolment[0].key=${delegatedEnrolmentKey.getOrElse("")}&delegatedEnrolment[0].taxIdentifier[0].name=${delegatedEnrolmentName0.getOrElse("")}" +
      s"&delegatedEnrolment[0].taxIdentifier[0].value=${delegatedEnrolmentValue0.getOrElse("")}&delegatedEnrolment[0].delegatedAuthRule=trust-auth" +
      s"&authorityId=$authorityId"
  }
}

object ServiceConfig {

  implicit val configLoader: ConfigLoader[ServiceConfig] = (rootConfig: Config, path: String) => {

    val config = rootConfig.getConfig(path)
    ServiceConfig(
      localport = config.getString("localport"),
      redirectUrl = config.getString("redirectUrl"),
      credentialStrength = config.getOptional("credentialStrength").getOrElse("strong"),
      confidenceLevel = config.getOptional("confidenceLevel").getOrElse("50"),
      affinityGroup = config.getOptional("affinityGroup").getOrElse("Individual"),
      enrolmentKey = config.getOptional("enrolment.key"),
      enrolmentIdentifierName0 = config.getOptional("enrolment.identifier0.name"),
      enrolmentIdentifierName1 = config.getOptional("enrolment.identifier1.name"),
      enrolmentsIdentifierValue0 = config.getOptional("enrolment.identifier0.value"),
      enrolmentsIdentifierValue1 = config.getOptional("enrolment.identifier1.value"),
      delegatedEnrolmentKey = config.getOptional("delegatedEnrolment.key"),
      delegatedEnrolmentName0 = config.getOptional("delegatedEnrolment.identifier0.name"),
      delegatedEnrolmentValue0 = config.getOptional("delegatedEnrolment.identifier0.value")
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

