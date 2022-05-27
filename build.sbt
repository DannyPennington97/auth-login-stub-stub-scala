import com.typesafe.sbt.packager.docker.DockerChmodType

name := """auth-login-stub-stub-scala"""
organization := "org.dannypen"

version := "0.0.1"

lazy val root = (project in file("."))

enablePlugins(PlayScala, DockerPlugin)

scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  guice,
  ws
)
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

PlayKeys.playDefaultPort := 4200

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "org.dannypen.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "org.dannypen.binders._"

// Workaround as per https://github.com/lightbend/sbt-reactive-app/issues/177
Universal / javaOptions ++= Seq(
  "-Dpidfile.path=/dev/null"
)

Docker / maintainer := "daniel.pennington@digital.hmrc.gov.uk"
dockerChmodType := DockerChmodType.UserGroupWriteExecute
dockerBaseImage := "openjdk:11"
dockerExposedPorts := Seq(4200)
dockerEnvVars := Map("ALSHOST" -> "127.0.0.1")
