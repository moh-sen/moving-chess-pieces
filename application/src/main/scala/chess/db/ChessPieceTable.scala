package chess.db

import cats.data.NonEmptyList

object ChessPieceTable {
  val table: TableColumns[PieceEntity] =
    TableColumns[PieceEntity](
      "chess_pieces",
      NonEmptyList.of(
        "id",
        "piece_type",
        "piece_row",
        "piece_col",
        "is_removed"
      )
    )

}
