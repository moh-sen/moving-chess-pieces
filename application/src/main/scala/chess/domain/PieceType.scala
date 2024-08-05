package chess.domain

import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait PieceType extends EnumEntry {}

object PieceType extends Enum[PieceType] with CirceEnum[PieceType] {

  private type T = PieceType

  case object ROOK   extends T
  case object BISHOP extends T

  override def values: IndexedSeq[T] = findValues
}
