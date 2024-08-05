package chess.api.model

import chess.api.{BaseSchemas, BasicCodecs}
import chess.domain.{PieceId, Position}
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.EndpointIO.Example
import sttp.tapir.Schema

case class MoveChessPieceRequest(pieceId: PieceId, position: Position)

object MoveChessPieceRequest {
  private lazy val movePiece = Example(
    value = MoveChessPieceRequest(
      pieceId = PieceId("be0914f4-7f15-11ee-b962-0242ac120002"),
      position = Position(row = 5, col = 5)
    ),
    name = None,
    summary = Some("move the rook"),
    description = Some(
      "This is an example of MoveChessPieceRequest, which will move an existing piece to the new position (5, 5) on the board."
    )
  )

  final lazy val examples: List[Example[MoveChessPieceRequest]] = List(movePiece)

  trait Codecs extends BasicCodecs {
    implicit val MoveChessPieceRequestCodec: Codec[MoveChessPieceRequest] = deriveCodec
  }

  trait Schemas extends BaseSchemas {
    implicit lazy val MoveChessPieceRequestSchema: Schema[MoveChessPieceRequest] = Schema.derived[MoveChessPieceRequest]
  }
}
