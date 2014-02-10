package controllers

import play.api.mvc._
import models.Entity
import models.Entity.Result
import play.api.mvc.Result

object Application extends Controller {

  def index = Action {

    val entity = new Entity.DBUtils()
    val rs= entity.getReferenceById("2")

    entity.updateReferenceById(rs.data)

    val jsonString = rs.toJson()

    Ok(jsonString)
  }
}