import chess.api.{RestApi, RestApiLive}
import chess.config.{AppConfig, HttpConfig}
import chess.db.{ChessRepositoryLive, TransactorLive}
import chess.service.ChessServiceLive
import zio.http.Server
import zio.logging.backend.SLF4J
import zio.{Runtime, Scope, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

object App extends ZIOAppDefault {

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  private lazy val serverWithRoutes = for {
    routes <- RestApiLive.routes
    port   <- Server.install(routes)
    _      <- ZIO.logInfo(s"=== Ready to service HTTP requests on port [${port.toString}]")
  } yield ()

  private lazy val serverConfigLayer: ZLayer[HttpConfig, Nothing, Server.Config] = ZLayer {
    for {
      port <- ZIO.serviceWith[HttpConfig](_.port)
    } yield Server.Config.default.port(port)
  }
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    (serverWithRoutes *> ZIO.never)
      .provideSome[RestApi](
        serverConfigLayer,
        Server.live,
        AppConfig.httpConfigLayer
      )
      .provide(
        AppConfig.dbConfigLayer,
        RestApiLive.layer,
        ChessServiceLive.layer,
        ChessRepositoryLive.layer,
        TransactorLive.layer,
        TransactorLive.dataSourceLayer
      )
}
