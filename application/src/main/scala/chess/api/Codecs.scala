package chess.api

import chess.api.model.{AddChessPieceRequest, AddChessPieceResponse, MoveChessPieceRequest}
import chess.domain.{PieceId, Position}
import io.circe.generic.semiauto.deriveCodec
import io.circe.{Codec, Decoder, Encoder, KeyDecoder, KeyEncoder}

trait RequestCodecs extends BasicCodecs with AddChessPieceRequest.Codecs with MoveChessPieceRequest.Codecs

trait ResponseCodecs extends BasicCodecs with AddChessPieceResponse.Codecs

trait BasicCodecs {
  implicit val clientIdKeyEncoder: KeyEncoder[PieceId] = (clientId: PieceId) => clientId.value
  implicit val clientIdKeyDecoder: KeyDecoder[PieceId] = value => Some(PieceId(value))
  implicit val clientIdCodec: Codec[PieceId] =
    Codec.from(Decoder[String].map(PieceId), Encoder[String].contramap(_.value))

  implicit val positionCodec: Codec[Position] = deriveCodec
}
