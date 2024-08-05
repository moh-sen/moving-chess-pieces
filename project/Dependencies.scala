import sbt.{ModuleID, *}

object Dependencies {

  private val config = Seq(
    "com.typesafe"           % "config"     % "1.4.3",
    "com.github.pureconfig" %% "pureconfig" % "0.17.6"
  )

  lazy val zio: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio"       % "2.0.21",
    "dev.zio" %% "zio-kafka" % "2.7.4"
  )

  lazy val tapir: Seq[ModuleID] = Seq(
    "tapir-json-circe",
    "tapir-openapi-docs",
    "tapir-swagger-ui-bundle",
    "tapir-zio",
    "tapir-zio-http-server"
  ).map("com.softwaremill.sttp.tapir" %% _ % "1.10.15")

  lazy val enumeration: Seq[ModuleID] = Seq("enumeratum", "enumeratum-circe").map("com.beachape" %% _ % "1.7.2")

  lazy val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-parser"         % "0.14.9",
    "io.circe" %% "circe-generic-extras" % "0.14.4"
  )

  lazy val db: Seq[ModuleID] = Seq(
    "org.tpolecat" %% "doobie-hikari"        % "1.0.0-RC4",
    "mysql"         % "mysql-connector-java" % "8.0.33"
  )

  lazy val zioInteropCats: Seq[ModuleID] = Seq("dev.zio" %% "zio-interop-cats" % "23.1.0.2")

  lazy val logging: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio-logging"       % "2.3.0",
    "dev.zio" %% "zio-logging-slf4j" % "2.3.0"
  ).map(
    _ excludeAll ExclusionRule("org.slf4j", "slf4j-api")
  ) ++ Seq(
    "ch.qos.logback"             % "logback-classic"          % "1.5.6" exclude ("org.slf4j", "slf4j-api"),
    "ch.qos.logback.contrib"     % "logback-json-classic"     % "0.1.5" exclude ("ch.qos.logback", "logback-classic"),
    "ch.qos.logback.contrib"     % "logback-jackson"          % "0.1.5" exclude ("ch.qos.logback", "logback-core"),
    "net.logstash.logback"       % "logstash-logback-encoder" % "7.2" excludeAll ExclusionRule("ch.qos.logback"),
    "com.fasterxml.jackson.core" % "jackson-databind"         % "2.17.0",
    "org.slf4j"                  % "slf4j-api"                % "2.0.12"
  )

  val application: Seq[ModuleID] = zio ++ tapir ++ config ++ enumeration ++ circe ++ db ++ zioInteropCats ++ logging
  val client: Seq[ModuleID]      = config ++ zio
}
