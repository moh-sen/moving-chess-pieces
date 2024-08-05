package chess.api.errorhandling

import chess.api.errorhandling.ResponseErrors.InvalidArgumentsError
import chess.domain.{ApplicationError, DBOperationError, InvalidRequestError, NotFoundError}
import sttp.model.StatusCode
import sttp.tapir.EndpointOutput
import sttp.tapir.json.circe._
import sttp.tapir.ztapir._

object OneOfVariants extends ResponseErrors.Schemas with ResponseErrors.Codecs {

  lazy val invalidArgumentsErrorVariant: EndpointOutput.OneOfVariant[ResponseErrors.InvalidArgumentsError] =
    oneOfVariant(
      StatusCode.BadRequest,
      jsonBody[ResponseErrors.InvalidArgumentsError]
        .description("Client has specified invalid argument.")
        .example(InvalidArgumentsError("Column cannot be less than 0 or more than 7."))
    )

  lazy val notFoundErrorVariant: EndpointOutput.OneOfVariant[ResponseErrors.NotFoundError] = oneOfVariant(
    StatusCode.NotFound,
    jsonBody[ResponseErrors.NotFoundError]
      .description("Chess piece not found.")
      .example(ResponseErrors.NotFoundError("Chess piece doesn't exist."))
  )

  lazy val unknownErrorVariant: EndpointOutput.OneOfVariant[ResponseErrors.UnknownError] = oneOfVariant(
    StatusCode.InternalServerError,
    jsonBody[ResponseErrors.UnknownError]
      .description("Unknown error")
      .example(ResponseErrors.UnknownError("unknown error message"))
  )

  lazy val internalErrorVariant: EndpointOutput.OneOfVariant[ResponseErrors.InternalServerError] = oneOfVariant(
    StatusCode.InternalServerError,
    jsonBody[ResponseErrors.InternalServerError]
      .description("internal server error")
      .example(ResponseErrors.InternalServerError("internal error server"))
  )

  lazy val handleApplicationError: ApplicationError => ResponseErrors = {
    case error: InvalidRequestError =>
      ResponseErrors.InvalidArgumentsError(error.message)

    case error: DBOperationError =>
      ResponseErrors.InternalServerError(error.message)

    case error: NotFoundError =>
      ResponseErrors.NotFoundError(error.message)

    case e =>
      ResponseErrors.UnknownError(e.message)
  }
}
