package chess.db

import chess.domain._
import doobie.{Transactor, Update}
import doobie.implicits._
import zio.interop.catz._
import zio.{IO, Task, URLayer, ZIO, ZLayer}

trait ChessRepository {
  def addPiece(entity: PieceEntity): IO[DatabaseError, Unit]

  def deletePiece(id: String): IO[DatabaseError, Int]

  def updatePiecePosition(id: String, row: Int, col: Int): IO[DatabaseError, Unit]

  def isPositionTaken(row: Int, col: Int): IO[DatabaseError, Boolean]

  def retrievePiece(id: String): IO[DatabaseError, Seq[(PieceType, Position)]]
}

case class ChessRepositoryLive(tx: Transactor[Task]) extends ChessRepository {
  override def addPiece(entity: PieceEntity): IO[DatabaseError, Unit] = {
    ChessPieceTable.table
      .insert(entity)
      .map(_ => ())
      .transact(tx)
      .catchAll(t =>
        ZIO.fail(
          DBOperationError(
            s"Error occurred while executing SQL statement to add a chess piece of type '${entity.pieceType}' to the" +
              s"position at row '${entity.piece_row}' and column '${entity.piece_col}'.",
            t
          )
        )
      )
  }

  override def deletePiece(id: String): IO[DatabaseError, Int] = {
    val sql =
      s"""
           UPDATE ${ChessPieceTable.table.tableName}
           SET is_removed = ?
           WHERE id       = '$id'
         """
    Update[Boolean](sql)
      .run(true)
      .transact(tx)
      .catchAll(t =>
        ZIO.fail(
          DBOperationError(
            s"Error occurred while executing SQL statement to mark the chess piece with id '$id' as removed",
            t
          )
        )
      )
  }

  override def updatePiecePosition(id: String, row: Int, col: Int): IO[DatabaseError, Unit] = {
    val sql =
      s"""
           UPDATE ${ChessPieceTable.table.tableName}
           SET piece_row = ?, piece_col = ?
           WHERE id       = '$id'
         """
    Update[(Int, Int)](sql)
      .run(row, col)
      .map(_ => ())
      .transact(tx)
      .catchAll(t =>
        ZIO.fail(
          DBOperationError(
            s"Error occurred while executing SQL statement to update the position of the chess piece with id" +
              s"'$id' to new row '$row' and column '$col'.",
            t
          )
        )
      )
  }

  override def isPositionTaken(row: Int, col: Int): IO[DatabaseError, Boolean] = {
    ChessPieceTable.table
      .select(
        fr"""
           WHERE piece_row = $row
            AND piece_col = $col
            AND is_removed = false
          """
      )
      .to[Seq]
      .map(_.nonEmpty)
      .transact(tx)
      .catchAll(t =>
        ZIO.fail(
          DBOperationError(
            s"Error occurred while executing query to check if the position at row '$row' and column '$col' is taken.",
            t
          )
        )
      )
  }

  override def retrievePiece(id: String): IO[DatabaseError, Seq[(PieceType, Position)]] = {
    ChessPieceTable.table
      .select(
        fr"""
           WHERE id = $id
           AND is_removed = false
          """
      )
      .to[List]
      .map { es =>
        es.map { e =>
          (PieceType.withName(e.pieceType), Position(e.piece_row, e.piece_col))
        }
      }
      .transact(tx)
      .catchAll(t =>
        ZIO.fail(
          DBOperationError(
            s"Error occurred while executing query to check if the piece with id '$id' exists.",
            t
          )
        )
      )
  }
}

object ChessRepositoryLive {
  val layer: URLayer[Transactor[Task], ChessRepository] = ZLayer {
    ZIO.serviceWith[Transactor[Task]](ChessRepositoryLive(_))
  }
}
