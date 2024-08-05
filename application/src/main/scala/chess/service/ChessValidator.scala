package chess.service

import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import cats.implicits._
import chess.domain.{ApplicationError, InvalidRequestError, PieceType, Position}
import zio.{IO, ZIO}

object ChessValidator {

  private type ValidationResult[T] = ValidatedNel[InvalidRequestError, T]
  def isMoveValid(
    chessType: PieceType,
    currentPosition: Position,
    nextPosition: Position
  ): IO[ApplicationError, Unit] = {
    (
      validateRowIndex(nextPosition.row),
      validateColIndex(nextPosition.col),
      chessType match {
        case PieceType.ROOK   => validateRook(currentPosition, nextPosition)
        case PieceType.BISHOP => validateBishop(currentPosition, nextPosition)
      }
    ).mapN((_, _, _) => ())
  }

  def isPositionValid(
    position: Position
  ): IO[ApplicationError, Unit] = {
    (
      validateRowIndex(position.row),
      validateColIndex(position.col)
    ).mapN((_, _) => ())
  }

  private[ChessValidator] def validateRowIndex(row: Int): ValidationResult[Unit] =
    if (0 > row || row > 7)
      InvalidRequestError("Row cannot be less than 0 or more than 7.").invalidNel
    else Valid(())

  private[ChessValidator] def validateColIndex(col: Int): ValidationResult[Unit] =
    if (0 > col || col > 7)
      InvalidRequestError("Column cannot be less than 0 or more than 7.").invalidNel
    else Valid(())

  private[ChessValidator] def validateBishop(current: Position, next: Position): ValidationResult[Unit] =
    if (Math.abs(current.row - next.row) == Math.abs(current.col - next.col))
      Valid(())
    else InvalidRequestError(s"Invalid move for Bishop from $current to $next").invalidNel

  private[ChessValidator] def validateRook(current: Position, next: Position): ValidationResult[Unit] =
    if ((Math.abs(current.row - next.row) == 0) || Math.abs(current.col - next.col) == 0)
      Valid(())
    else
      InvalidRequestError(s"Invalid move for Rook from $current to $next").invalidNel

  implicit def toZIO[A](value: ValidationResult[A]): IO[ApplicationError, A] =
    value match {
      case Valid(a) => ZIO.succeed(a)
      case Invalid(nel) =>
        ZIO.fail(
          InvalidRequestError(
            nel.toList.map(_.message).mkString(", ")
          )
        )
    }
}
