package chess.domain

sealed trait ApplicationError extends Product with Serializable {
  val message: String
  def getCause: Throwable
}

final case class InvalidRequestError(override val message: String) extends ApplicationError {
  override def getCause: Throwable = new IllegalArgumentException(message)
}

final case class NotFoundError(override val message: String) extends ApplicationError {
  override def getCause: Throwable = new RuntimeException(message)
}

sealed trait DatabaseError extends ApplicationError

final case class DBOperationError(errorMessage: String, cause: Throwable) extends DatabaseError {
  override val message: String = errorMessage

  override def getCause: Throwable = new RuntimeException(errorMessage, cause)
}
final case class EntityNotFoundError(errorMessage: String, cause: Throwable) extends DatabaseError {
  override val message: String = errorMessage

  override def getCause: Throwable = new RuntimeException(errorMessage, cause)
}
