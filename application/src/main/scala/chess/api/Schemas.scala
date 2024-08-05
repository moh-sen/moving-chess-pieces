package chess.api

import chess.api.model.{AddChessPieceRequest, AddChessPieceResponse, MoveChessPieceRequest}
import chess.domain.{PieceId, PieceType, Position}
import sttp.tapir.Schema

trait RequestSchemas extends BaseSchemas with AddChessPieceRequest.Schemas with MoveChessPieceRequest.Schemas

trait ResponseSchemas extends AddChessPieceResponse.Schemas

trait BaseSchemas {
  implicit lazy val pieceTypeSchema: Schema[PieceType] = Schema.derivedEnumeration[PieceType].defaultStringBased
  implicit lazy val pieceIdSchema: Schema[PieceId]     = Schema.derived
  implicit lazy val positionSchema: Schema[Position]   = Schema.derived
}
