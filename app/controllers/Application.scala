package controllers

import java.util.Date

import models.{Vote, Votes}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

object Application extends Controller {
  def logger = Logger.logger

  val voteForm = Form(
    single(
      "vote" -> number
    )
  )

  def index = Action {
    Ok(views.html.index(Votes.all, voteForm))
  }

  def save = Action { implicit request =>
    val vote = voteForm.bindFromRequest.get
    Votes.create(Vote(vote))
    Redirect(routes.Application.index())
  }
}
