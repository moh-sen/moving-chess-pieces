package chess.api.model

import chess.domain.{PieceType, Position}
import chess.domain.PieceType.{BISHOP, ROOK}
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.EndpointIO.Example
import sttp.tapir.Schema
import chess.api.{BaseSchemas, BasicCodecs}
case class AddChessPieceRequest(pieceType: PieceType, position: Position)

object AddChessPieceRequest {
  private lazy val addRook = Example(
    value = AddChessPieceRequest(pieceType = ROOK, position = Position(row = 0, col = 0)),
    name = None,
    summary = Some("add rook"),
    description = Some(
      "This is an example of an AddChessPieceRequest, which will add a new rook at the position (0, 0) on the board."
    )
  )

  private lazy val addBishop = Example(
    value = AddChessPieceRequest(pieceType = BISHOP, position = Position(row = 7, col = 7)),
    name = None,
    summary = Some("add a bishop"),
    description = Some(
      "This is an example of an AddChessPieceRequest, which will add a new bishop at the position (7, 7) on the board."
    )
  )

  final lazy val examples: List[Example[AddChessPieceRequest]] = List(
    addRook,
    addBishop
  )

  trait Codecs extends BasicCodecs {
    implicit val AddChessFieldRequestCodec: Codec[AddChessPieceRequest] = deriveCodec
  }

  trait Schemas extends BaseSchemas {
    implicit lazy val AddChessFieldRequestSchema: Schema[AddChessPieceRequest] = Schema.derived[AddChessPieceRequest]
  }
}
