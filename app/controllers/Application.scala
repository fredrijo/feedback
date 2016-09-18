package controllers

import models.{Vote, Votes}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

object Application extends Controller {
  def logger = Logger.logger

  val voteForm = Form(single("vote" -> number))

  def index = Action { implicit request =>
    Ok(views.html.index(Votes.all, Votes.today, Votes.best))
  }

  def save = Action { implicit request =>
    val vote = voteForm.bindFromRequest.get
    Votes.create(Vote(vote))
    ajaxCall()
    Redirect(routes.Application.index())
  }

  def ajaxCall() = Action { implicit request =>
    val data = Map(
      "1" -> Votes.today.count(_.vote == 1.0),
      "2" -> Votes.today.count(_.vote == 2.0),
      "3" -> Votes.today.count(_.vote == 3.0))
    println(Json.toJson(data))
    Ok(Json.toJson(data))
  }
}
