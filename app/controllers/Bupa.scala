package controllers

import play.api.mvc._
import models.Entity
import models.Entity._
import models.Entity.Result
import models.Entity.ImageInfo
import models.Entity.ReferenceInfo

object Bupa extends Controller {

  val _entity = new Entity.DBUtils()
  val _logger = play.api.Logger

  def login(user: String, password: String) = Action {
    request => {

      _logger.info("== Try Login ==")
      _logger.info(s"User: $user")
//      _logger.info(s"Password: $password")

      val rs = _entity.login(user, password)
      val status = rs.toJson()
      Ok(status)
    }
  }

  def getRefernceInfo(id: String) = Action {
    request => {
      val rs = _entity.getReferenceById(id)
      Ok(rs.toJson())
    }
  }

  // Update refernce information.
  def updateReferenceInfo() = Action (parse.json){
    request => {

      try {

        _logger.info("== Update Reference Info ==")

        val reference = request.body.as[ReferenceInfo]

        // Write request body...
        _logger.info(reference.toJson())

        val rs = _entity.updateReferenceInfo(reference)

        println(reference.scanDate)

        val status = rs.toJson()

        Ok(status)

      } catch {

        case e: Throwable => {

          // Write log...
          _logger.error(e.getMessage, e)

          // Create message
          val rs = new Result(success = false, message = e.getMessage, data = new ReferenceInfo())

          // Return error message
          Ok(rs.toJson())
        }

      }
    }
  }

  // Insert images...
  def insertImageInfos() = Action (parse.text){
    request => {

      val text = request.body

      _logger.info("== Insert Image Infos ==")
      _logger.info(s"Request: $text")

      val json = new JsonUtils[Array[ImageInfo]]()
      val obj = json.fromJson(text)
      val rs = _entity.insertImageInfos(obj)
      val status = rs.toJson()

      Ok(status)

//      val images = request.body.as[Array[ImageInfo]]
//      val rs = _entity.insertImageInfos(images)
//      val status = rs.toJson()
//
//      Ok(status)
    }
  }
}