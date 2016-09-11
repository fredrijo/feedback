package models

import java.sql.Timestamp
import java.sql.Date

import play.api.Play.current

import scala.slick.driver.PostgresDriver.simple._

case class Vote(vote: Int, date: Option[Date] = None, id: Option[Int] = None)

class Votes(tag: Tag) extends Table[Vote](tag, "VOTES") {

  // The value from 1-3, where 1=bad, 2=ok and 3=great!
  def id = column[Option[Int]]("ID", O.AutoInc, O.NotNull)

  // The value from 1-3, where 1=bad, 2=ok and 3=great!
  def vote = column[Int]("VOTE", O.NotNull)

  // The date can't be null
  def date = column[Option[Date]]("DATE", O.NotNull, O.AutoInc, O.DBType("timestamp default now()"))

  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a User
  def * = (vote, date, id) <> (Vote.tupled, Vote.unapply)
}

object Votes {
  val db = play.api.db.slick.DB
  val votes = TableQuery[Votes]

  def all: List[Vote] = db.withSession { implicit session =>
    votes.sortBy(_.date.desc).list
  }

  def create(vote: Vote) = db.withTransaction { implicit session =>
    votes += vote
    println(s"inserted $vote")
  }

}