package chess.db

import cats.data.NonEmptyList
import doobie._
import doobie.implicits.toSqlInterpolator
import doobie.util.fragments.parentheses
import doobie.util.meta.LegacyInstantMetaInstance

case class TableColumns[A: Read: Write](tableName: String, columns: NonEmptyList[String])
    extends LegacyInstantMetaInstance {
  private val tableNameFragment: Fragment = Fragment.const(tableName)

  private val fieldsFragment: Fragment = Fragment.const(columns.toList.mkString(","))

  private val insertFragment: Fragment =
    fr"insert into $tableNameFragment ($fieldsFragment)" ++
      fr"values" ++
      parentheses(Fragment.const(List.fill(columns.size)("?").mkString(",")))

  def insert(value: A): doobie.ConnectionIO[Int] = Update[A](insertFragment.internals.sql).run(value)

  def select(where: Fragment): Query0[A] =
    fr"select $fieldsFragment from $tableNameFragment $where".query[A]
}
