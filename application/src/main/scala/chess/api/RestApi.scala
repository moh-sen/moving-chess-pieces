package chess.api

import chess.api.errorhandling.{OneOfVariants, ResponseErrors}
import chess.api.model.{AddChessPieceRequest, AddChessPieceResponse, MoveChessPieceRequest}
import chess.domain._
import chess.service.ChessService
import sttp.apispec.openapi.circe.yaml.RichOpenAPI
import sttp.model.StatusCode
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.SwaggerUI
import sttp.tapir.ztapir._
import sttp.tapir.{Endpoint, EndpointInput, oneOf, path, statusCode}
import zio.http.{Response, Routes}
import zio.{Task, ZIO, ZLayer}

trait RestApi {
  def httpRoutes: ZIO[Any, Nothing, Routes[RestApi, Response]]
}

case class RestApiLive(chessService: ChessService)
    extends RestApi
    with RequestCodecs
    with RequestSchemas
    with ResponseCodecs
    with ResponseSchemas {

  private val baseEndpoint = endpoint.in("chess")

  private lazy val postEndpoint: Endpoint[Unit, AddChessPieceRequest, ResponseErrors, AddChessPieceResponse, Any] =
    baseEndpoint.post
      .in(
        jsonBody[AddChessPieceRequest]
          .examples(AddChessPieceRequest.examples)
      )
      .summary("Add a new chess piece")
      .description("Request to add a new chess piece to the board at a given free position.")
      .out(jsonBody[AddChessPieceResponse])
      .errorOut(
        oneOf[ResponseErrors](
          OneOfVariants.invalidArgumentsErrorVariant,
          OneOfVariants.internalErrorVariant,
          OneOfVariants.unknownErrorVariant
        )
      )

  private lazy val deleteEndpoint: Endpoint[Unit, PieceId, ResponseErrors, Unit, Any] =
    baseEndpoint.delete
      .in("piece" / pieceIdInput)
      .summary("Delete a chess piece")
      .out(
        statusCode(StatusCode.NoContent)
          .description("Request to remove a chess piece from the board at a given position.")
      )
      .errorOut(
        oneOf[ResponseErrors](
          OneOfVariants.invalidArgumentsErrorVariant,
          OneOfVariants.notFoundErrorVariant,
          OneOfVariants.internalErrorVariant,
          OneOfVariants.unknownErrorVariant
        )
      )

  private lazy val putEndpoint =
    baseEndpoint.put
      .in(
        jsonBody[MoveChessPieceRequest]
          .examples(MoveChessPieceRequest.examples)
      )
      .summary("Move a chess piece")
      .out(
        statusCode(StatusCode.NoContent)
          .description("Request to move a chess piece to the new position on the board.")
      )
      .errorOut(
        oneOf[ResponseErrors](
          OneOfVariants.invalidArgumentsErrorVariant,
          OneOfVariants.internalErrorVariant,
          OneOfVariants.notFoundErrorVariant,
          OneOfVariants.unknownErrorVariant
        )
      )

  private lazy val pieceIdInput: EndpointInput.PathCapture[PieceId] =
    path[String]("pieceId")
      .description("Identifier of the chess piece")
      .example("d37de40c-7a33-4c0b-86de-8adf941325bd")
      .map(value => PieceId(value))(pieceId => pieceId.value)

  private val routes: Routes[Any, Response] = ZioHttpInterpreter().toHttp(
    List(
      postEndpoint.zServerLogic { request =>
        chessService
          .addChessPiece(request.pieceType, request.position)
          .flatMap { id =>
            ZIO.succeed(AddChessPieceResponse(pieceId = PieceId(id)))
          }
          .mapError(OneOfVariants.handleApplicationError)
      },
      deleteEndpoint.zServerLogic { id =>
        chessService.removeChessPiece(id).mapError(OneOfVariants.handleApplicationError)
      },
      putEndpoint.zServerLogic { request =>
        chessService.moveChessPiece(request.pieceId, request.position).mapError(OneOfVariants.handleApplicationError)
      }
    )
  )

  private val endPoints = List(
    postEndpoint,
    deleteEndpoint,
    putEndpoint
  ).map(_.tags(List("Chess Endpoints")))

  override def httpRoutes: ZIO[Any, Nothing, Routes[RestApi, Response]] =
    for {
      openApi       <- ZIO.succeed(OpenAPIDocsInterpreter().toOpenAPI(endPoints, "Chess Service", "0.1"))
      routesHttp    <- ZIO.succeed(routes)
      endPointsHttp <- ZIO.succeed(ZioHttpInterpreter().toHttp(SwaggerUI[Task](openApi.toYaml)))
    } yield routesHttp ++ endPointsHttp
}

object RestApiLive {
  val layer: ZLayer[ChessService, Nothing, RestApiLive] = ZLayer {
    ZIO.serviceWith[ChessService](RestApiLive(_))
  }

  val routes: ZIO[RestApi, Nothing, Routes[RestApi, Response]] = ZIO.serviceWithZIO[RestApi](_.httpRoutes)
}
