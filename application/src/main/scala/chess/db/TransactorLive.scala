package chess.db

import chess.config.DbConfig
import com.zaxxer.hikari.HikariDataSource
import doobie.hikari.HikariTransactor
import doobie.Transactor
import zio._
import zio.ZIO.logDebug
import zio.interop.catz._
import zio.interop.catz.implicits.rts

object TransactorLive {

  val dataSourceLayer: ZLayer[DbConfig, Throwable, HikariDataSource] = ZLayer.scoped(
    for {
      _      <- logDebug("Creating HikariDataSource")
      config <- ZIO.service[DbConfig]
      ds     <- ZIO.fromAutoCloseable(ZIO.attemptBlockingIO(createDataSource(config)))
      _      <- logDebug("Created HikariDataSource")
    } yield ds
  )

  val layer: ZLayer[HikariDataSource, Nothing, Transactor[Task]] = ZLayer(
    for {
      _                <- logDebug("Creating transactor")
      ds               <- ZIO.service[HikariDataSource]
      blockingExecutor <- ZIO.blockingExecutor
      transactor = HikariTransactor[Task](ds, blockingExecutor.asExecutionContext)
      _ <- logDebug("Created transactor")
    } yield transactor
  )

  private def createDataSource(config: DbConfig): HikariDataSource = {
    val ds = new HikariDataSource()
    ds.setJdbcUrl(config.connectionString)
    ds.setPoolName(config.poolName)
    ds.setDriverClassName(config.driver)
    ds.setUsername(config.user)
    ds.setPassword(config.password)
    ds.setMinimumIdle(config.minimumPoolSize)
    ds.setMaximumPoolSize(config.maximumPoolSize)
    ds
  }
}
