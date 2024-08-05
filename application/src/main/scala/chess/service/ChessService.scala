package chess.service

import chess.db.{ChessRepository, PieceEntity}
import chess.domain._
import zio.{IO, URLayer, ZIO, ZLayer}

import java.util.UUID

trait ChessService {
  def addChessPiece(pieceType: PieceType, position: Position): IO[ApplicationError, String]

  def removeChessPiece(id: PieceId): IO[ApplicationError, Unit]

  def moveChessPiece(id: PieceId, nextPosition: Position): IO[ApplicationError, Unit]
}

case class ChessServiceLive(repository: ChessRepository) extends ChessService {
  override def addChessPiece(pieceType: PieceType, position: Position): IO[ApplicationError, String] = for {
    _       <- ChessValidator.isPositionValid(position)
    isTaken <- repository.isPositionTaken(position.row, position.col)
    _ <- ZIO
      .fail(
        InvalidRequestError(
          s"Position with row '${position.row} and column '${position.col}' is already taken."
        )
      )
      .when(isTaken)
    id = UUID.randomUUID().toString
    _ <- repository.addPiece(
      PieceEntity(id, pieceType.entryName, position.row, position.col, isRemoved = false)
    )
  } yield id

  override def removeChessPiece(id: PieceId): IO[ApplicationError, Unit] = for {
    removedEntitiesCount <- repository.deletePiece(id.value).tapError(e => ZIO.logError(e.getCause.getMessage))
    _ <- ZIO
      .fail(InvalidRequestError(s"The piece with id '${id.value}' does not exist."))
      .when(removedEntitiesCount == 0)
  } yield ()

  override def moveChessPiece(id: PieceId, nextPosition: Position): IO[ApplicationError, Unit] = for {
    pieceTypesPositions <- repository.retrievePiece(id.value).tapError(e => ZIO.logError(e.getCause.getMessage))
    _ <- ZIO
      .fail(InvalidRequestError(s"The piece with id '${id.value}' does not exist."))
      .when(pieceTypesPositions.isEmpty)
    (pieceType, position) = (pieceTypesPositions.head._1, pieceTypesPositions.head._2)
    _       <- ChessValidator.isMoveValid(pieceType, position, nextPosition)
    isTaken <- repository.isPositionTaken(nextPosition.row, nextPosition.col)
    _ <- ZIO
      .fail(
        InvalidRequestError(
          s"Destination position with row '${nextPosition.row} and column '${nextPosition.col}' is already taken."
        )
      )
      .when(isTaken)
    _ <- repository.updatePiecePosition(id.value, nextPosition.row, nextPosition.col)
  } yield ()
}

object ChessServiceLive {
  val layer: URLayer[ChessRepository, ChessService] = ZLayer {
    ZIO.serviceWith[ChessRepository](ChessServiceLive(_))
  }
}
