package chess.db

case class PieceEntity(id: String, pieceType: String, piece_row: Int, piece_col: Int, isRemoved: Boolean)
