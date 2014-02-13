package models

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.lifted.ProvenShape
import play.api.Play
import play.api.libs.json._
import java.util.Calendar

// Entity object
object Entity {

  // Default object's logger.
  val logger = play.api.Logger

  // Implicit json writer for..
  // * ReferenceInfo
  // * ImageInfo
  // * UserInfo
  implicit val w1 = Json.writes[ReferenceInfo]
  implicit val w2 = Json.writes[ImageInfo]
  implicit val w3 = Json.writes[UserInfo]

  // Implicit json reader for ..
  // * ReferenceInfo
  // * ImageInfo
  implicit val r1 = Json.reads[ImageInfo]
  implicit val r2 = Json.reads[ReferenceInfo]

  // Read connection info from play configuration file.
  // * Driver
  // * Url
  // * User
  // * Password
  val driver = Play.current.configuration.getString("db.default.driver").get
  val url = Play.current.configuration.getString("db.default.url").get
  val user = Play.current.configuration.getString("db.default.user").get
  var password = Play.current.configuration.getString("db.default.password").get

  // Tables
  // * RefernceInfos
  // * ImageInfos
  val referenceInfos = TableQuery[ReferenceInfos]
  var imageInfos = TableQuery[ImageInfos]
  var userInfos = TableQuery[UserInfos]

  // Manaul serialize
  // * sucess : String
  // * message : String
  // * data : ImageInfo, ReferenceInfo, Array[ImageInfo]
  case class Result[T](success: Boolean, message: String, data: T) {
    def toJson()(implicit writes: Writes[T]): String = {
      def value = Json.toJson(Map(
        "success" -> Json.toJson(success),
        "message" -> Json.toJson(message),
        "data" -> Json.toJson(data)(writes)
      ))
      Json.stringify(value)
    }
  }

  // Json utility
  // Parse string into Scala object.
  class JsonUtils[T]() {
    def fromJson(json: String)(implicit reads: Reads[T]): T = {
      val value = Json.parse(json)
      value.as[T]
    }
  }

  // Reference Definition
  // MySql persistance object
  // * reference
  // * page
  // * scanDate
  // * scanUser
  // * status
  case class ReferenceInfo(
                            reference: String,
                            page: Int,
                            scanDate: java.sql.Date,
                            scanUser: String,
                            status: String) {

    def this() = this(null, 0, new java.sql.Date(new java.util.Date(0L).getTime()),null, null)

    // Convert object into json string.
    def toJson(): String = {
      val writes = Json.writes[ReferenceInfo]
      val value = Json.toJson(this)(writes)
      Json.stringify(value)
    }
  }

  // Slick definition for "breferenceinfo"
  // Map to ReferenceInfo
  // * reference
  // * page
  // * scanDate
  // * scanUser
  // * status
  class ReferenceInfos(tag: Tag) extends Table[ReferenceInfo](tag, "breferenceinfo") {

    // Primary key
    // Sometime call case id.
    def reference = column[String]("Reference", O.PrimaryKey, O.AutoInc)

    // Number of pages
    def page = column[Int]("Page")

    // Scan date
    def scanDate = column[java.sql.Date]("ScanDate")

    // Scan by
    def scanUser = column[String]("ScanUser")

    // Status [Receive, Register, Scan, Index, Indexing, Reject, Cancel, Admin]
    def status = column[String]("Status")

    // Default projection
    def * : ProvenShape[ReferenceInfo] =
      (reference, page, scanDate, scanUser, status) <>(ReferenceInfo.tupled, ReferenceInfo.unapply)
  }

  // Image information define as case class.
  // * id
  // * path
  // * documentTitle
  // * createDate
  // * isDelete
  // * page
  case class ImageInfo(
                        id: Int,
                        reference: String,
                        path: String,
                        documentTitle: String,
                        createDate: java.sql.Date,
                        isDelete: Boolean,
                        page: Int) {
  }

  // Field definition
  // Map to ImageInfo
  class ImageInfos(tag: Tag) extends Table[ImageInfo](tag, "bimageinfo") {

    // Primary key
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    // Foreign key
    def reference = column[String]("Reference")

    // Image page
    def path = column[String]("Path")

    // Document's name
    def documentTitle = column[String]("DocumentTitle")

    // Create date (Insert date)
    def createDate = column[java.sql.Date]("CreateDate")

    // Delete status
    def isDelete = column[Boolean]("IsDelete")

    // Number of pages
    def page = column[Int]("Page")

    // Default projection
    def * : ProvenShape[ImageInfo] =
      (id, reference, path, documentTitle, createDate, isDelete, page) <>(ImageInfo.tupled, ImageInfo.unapply)
  }

  // User property...
  case class UserInfo(user: String, password: String) {
    def this() = this("", "")
  }

  // User table...
  class UserInfos(tag: Tag) extends Table[UserInfo](tag, "buserinfo") {
    def userName = column[String]("Username")

    def password = column[String]("Password")

    def * = (userName, password) <>(UserInfo.tupled, UserInfo.unapply)
  }


  // Utility class for manage DB.
  // * ReferenceInfo
  // * ImageInfo
  class DBUtils() {

    // Get reference info by id (string)
    def getReferenceById(id: String): Result[ReferenceInfo] = {
      // Start session
      Database.forURL(url, driver = driver, user = user, password = password).withSession {
        implicit session: Session =>
        // Get refereince by "reference number" and "status"
          val query = for {
            c <- referenceInfos if c.reference === id
          } yield c

          if (query.list.length != 0) {

            val rs = query.first()

            logger.info("Found: " + rs)
            new Result(success = true, message = "", data = rs)
          } else {
            new Result(success = false, message = "", data = new ReferenceInfo())
          }
      }
    }

    def login(u: String, p: String): Result[UserInfo] = {
      Database.forURL(url, driver = driver, user = user, password = password).withSession {
        implicit session: Session =>
          val query = for {
            c <- userInfos if c.userName === u && c.password === p
          } yield c


          if (query.list.length == 0) {
            val empty = new UserInfo()
            new Result(success = false, message = "", empty)
          }
          else new Result(success = true, message = "", data = query.first())
      }
    }

    // Update reference info
    def updateReferenceInfo(info: ReferenceInfo): Result[ReferenceInfo] = {

      Database.forURL(url, driver = driver, user = user, password = password).withSession {
        implicit session: Session =>

          val scanDate = new java.sql.Date(Calendar.getInstance().getTime().getTime())

          def findById(id: String) = for {
            c <- referenceInfos if c.reference === id
          } yield (c.page, c.scanDate, c.scanUser, c.status)

          findById(info.reference)
            .update(info.page, scanDate , info.scanUser, info.status)

          new Result[ReferenceInfo](success = true, message = null, data = info)
      }
    }

    // Insert collection of image into database.
    def insertImageInfos(infos: Array[ImageInfo]): Result[Array[ImageInfo]] = {
      Database.forURL(url, driver = driver, user = user, password = password).withSession {
        implicit session: Session =>
          for (img <- infos) {
            imageInfos.insert(img)
          }
          new Result[Array[ImageInfo]](success = true, message = null, data = infos)
      }
    }
  }

}
