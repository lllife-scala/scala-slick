package controllers

import play.api.mvc._
import service.SocketService
import models.Entity.{ImageInfo, JsonUtils, ReferenceInfo, Result}
import models.Entity


object Bupa extends Controller {

  // Default
  // * Entity
  // * Logger
  val $entity = new Entity.DBUtils()
  val $logger = play.api.Logger

  // Login by
  // * User
  // * Password
  def login(user: String, password: String) = Action {
    request => {
      // Log request message
      // Show user name
      $logger.info("== Try Login ==")
      $logger.info(s"User: $user")

      // Process
      // Return json message
      // Use status Ok
      val rs = $entity.login(user, password)
      val status = rs.toJson()
      Ok(status)
    }
  }

  // Get refernce info by
  // * ID (String)
  def getRefernceInfo(id: String) = Action {
    request => {
      val rs = $entity.getReferenceById(id)
      Ok(rs.toJson())
    }
  }

  // Update refernce information.
  // Read update info from post body
  def updateReferenceInfo() = Action(parse.json) {
    request => {
      // Read as json
      // Then cast to RefernceInfo
      val reference = request.body.as[ReferenceInfo]

      try {
        // Log request message
        // Show case class constructor
        $logger.info("== Update ==")
        $logger.info(s"Refernce: $reference")

        // Update DB
        // Convert result as json string
        val rs = $entity.updateReferenceInfo(reference)
        val status = rs.toJson()

        // Broadcase message
        // For realtime monitor...
        //SocketService.send("{Hello world}")
        SocketService.send(status)


        // Return json message
        // With status Ok
        Ok(status)

      } catch {
        case e: Throwable => {
          // Log error
          // Generate error message
          // Return json string
          $logger.error(e.getMessage, e)
          val rs = new Result(success = false, message = e.getMessage, data = new ReferenceInfo())
          Ok(rs.toJson())
        }
      }
    }
  }

  // Insert collection of images
  // Read images from post body
  // Then cast info case class object
  // ImageInfo include
  // * ID
  // * Path
  // * DocumentTitle
  // * Reference
  // * CreateDate
  def insertImageInfos() = Action(parse.text) {
    request => {
      val text = request.body

      $logger.info("== Insert Image Infos ==")
      $logger.info(s"Request: $text")

      val json = new JsonUtils[Array[ImageInfo]]()
      val obj = json.fromJson(text)
      val rs = $entity.insertImageInfos(obj)
      val status = rs.toJson()

      Ok(status)
    }
  }
}