package controllers

import  play.api.mvc._

/**
 * Created by recovery on 2/11/14.
 */
object Application  extends  Controller{
  def index() = Action {
    Ok("I'm ok")
  }
}
