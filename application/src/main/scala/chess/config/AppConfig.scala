package chess.config

import pureconfig.ConfigSource
import pureconfig.generic.auto._
import zio.{ULayer, ZLayer}
case class HttpConfig(host: String, port: Int)
case class DbConfig(
  connectionString: String,
  poolName: String,
  user: String,
  password: String,
  driver: String,
  minimumPoolSize: Int,
  maximumPoolSize: Int
)
case class AppConfig(httpConfig: HttpConfig, dbConfig: DbConfig)

object AppConfig {
  private lazy val appConfig: AppConfig =
    ConfigSource.default.at("application").loadOrThrow[AppConfig]

  private val appConfigLayer: ULayer[AppConfig] = ZLayer.succeed(appConfig)
  val dbConfigLayer: ULayer[DbConfig]           = appConfigLayer.project(_.dbConfig)
  val httpConfigLayer: ULayer[HttpConfig]       = appConfigLayer.project(_.httpConfig)
}
