package chess.api.errorhandling

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Schema

sealed trait ResponseErrors

object ResponseErrors {

  case class InvalidArgumentsError(message: String) extends ResponseErrors

  case class NotFoundError(message: String) extends ResponseErrors

  case class InternalServerError(message: String) extends ResponseErrors

  case class UnknownError(status: String) extends ResponseErrors

  trait Codecs {
    implicit lazy val InvalidArgumentsErrorCodec: Codec[InvalidArgumentsError] = deriveCodec
    implicit lazy val NotFoundErrorCodec: Codec[NotFoundError]                 = deriveCodec
    implicit lazy val InternalServerErrorCodec: Codec[InternalServerError]     = deriveCodec
    implicit lazy val unknownErrorCodec: Codec[UnknownError]                   = deriveCodec
  }

  trait Schemas {
    implicit lazy val invalidArgumentsErrorSchema: Schema[InvalidArgumentsError] = Schema.derived
    implicit lazy val notFoundErrorSchema: Schema[NotFoundError]                 = Schema.derived
    implicit lazy val internalServerSchema: Schema[InternalServerError]          = Schema.derived
    implicit lazy val unknownErrorSchema: Schema[UnknownError]                   = Schema.derived
  }
}
