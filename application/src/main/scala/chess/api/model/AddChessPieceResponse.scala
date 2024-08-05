package chess.api.model

import chess.api.{BaseSchemas, BasicCodecs}
import chess.domain.PieceId
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Schema

case class AddChessPieceResponse(pieceId: PieceId)

object AddChessPieceResponse {
  lazy val example: AddChessPieceResponse =
    AddChessPieceResponse(pieceId = PieceId("be0914f4-7f15-11ee-b962-0242ac120002"))

  trait Codecs extends BasicCodecs {
    implicit val AddChessPieceResponseCodec: Codec[AddChessPieceResponse] = deriveCodec
  }

  trait Schemas extends BaseSchemas {
    implicit lazy val AddChessPieceResponseSchema: Schema[AddChessPieceResponse] = Schema.derived
  }
}
