package controllers

import play.api.mvc.{Action, Controller}

object Scan  extends Controller {

  def index = Action { request =>
    Ok(views.html.scan())
  }
}
