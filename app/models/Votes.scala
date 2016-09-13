package models

import java.text.SimpleDateFormat

import com.github.tototoshi.slick.PostgresJodaSupport._
import org.joda.time.LocalDate
import play.api.Play.current

import scala.slick.driver.PostgresDriver.simple._

case class Vote(vote: Int, date: LocalDate = LocalDate.now(), id: Option[Int] = None)

class Votes(tag: Tag) extends Table[Vote](tag, "VOTES") {

  // The value from 1-3, where 1=bad, 2=ok and 3=great!
  def id = column[Option[Int]]("ID", O.AutoInc, O.NotNull)

  // The value from 1-3, where 1=bad, 2=ok and 3=great!
  def vote = column[Int]("VOTE", O.NotNull)

  // The date can't be null
  def date = column[LocalDate]("DATE", O.NotNull)

  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a Vote
  def * = (vote, date, id) <> (Vote.tupled, Vote.unapply)
}

object Votes {
  private val db = play.api.db.slick.DB
  private val votes = TableQuery[Votes]

  def all: List[Vote] = db.withSession { implicit session =>
    votes.sortBy(_.date.desc).list
  }

  def create(vote: Vote) = db.withTransaction { implicit session =>
    votes += vote
    println(s"inserted $vote")
  }

  def today: List[Vote] = db.withSession { implicit session =>
    votes.filter(_.date === LocalDate.now()).list
  }

  def averages: Map[LocalDate, Double] = db.withSession { implicit session =>
    votes
      .groupBy(_.date)
      .map { case (key, query) =>
        val values = query.map(_.vote).list
        (key, values.sum.toDouble / values.size.toDouble)
      }.list.toMap
  }

  def best: (LocalDate, Double) = db.withSession { implicit session =>
    averages.toList.sortBy(_._2).reverse.head
  }
}