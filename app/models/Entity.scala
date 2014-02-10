package models

import scala.slick.driver.MySQLDriver.simple._
import java.sql.Date
import scala.slick.lifted.ProvenShape
import play.api.Play
import play.api.libs.json._

object Entity {

  implicit val w1 = Json.writes[ReferenceInfo]

  //  implicit val w2 = Json.writes[Result[ReferenceInfo]]

  case class Result[T](success: Boolean, message: String, data: T) {

    def toJson()(implicit writes: Writes[T]): String = {

      def value = Json.toJson(Map(
        "success" -> Json.toJson(success),
        "message" -> Json.toJson(message),
        "data" -> Json.toJson(data)(writes)
      ))

      Json.stringify(value)
    }

    //    def toJsonX() (implicit writes: Writes[Result[T]]): String = {
    //      def value = Json.toJson(this)(writes)
    //      Json.stringify(value)
    //    }
  }

  // Reference Definition
  case class ReferenceInfo
  (reference: String,
   page: Int,
   customerName: String,
   customerSurname: String,
   status: String) {

    def toJson(): String = {
      val writes = Json.writes[ReferenceInfo]
      val value = Json.toJson(this)(writes)
      Json.stringify(value)
    }
  }

  class ReferenceInfos(tag: Tag) extends Table[ReferenceInfo](tag, "breferenceinfo") {

    def reference = column[String]("Reference", O.PrimaryKey, O.AutoInc)

    def page = column[Int]("Page")

    def customerName = column[String]("CustomerName")

    def customerSurname = column[String]("CustomerSurname")

    def status = column[String]("Status")

    def * : ProvenShape[ReferenceInfo] =
      (reference, page, customerName, customerSurname, status) <>(ReferenceInfo.tupled, ReferenceInfo.unapply)
  }

  // Connection infos.
  val driver = Play.current.configuration.getString("db.default.driver").get
  val url = Play.current.configuration.getString("db.default.url").get
  val user = Play.current.configuration.getString("db.default.user").get
  var password = Play.current.configuration.getString("db.default.password").get

  // Tables
  val referenceInfos = TableQuery[ReferenceInfos]

  class DBUtils() {

    def getReferenceById(id: String): Result[ReferenceInfo] = {
      Database.forURL(url, driver = driver, user = user, password = password).withSession {
        implicit session: Session =>

          val query = for {
            c <- referenceInfos if c.reference === id
          } yield c

          val info = query.first()
          val rs = new Result(success = info != null, message = null, data = info)
          return rs
      }
    }

    def updateReferenceById(info: ReferenceInfo): Result[ReferenceInfo] = {
      Database.forURL(url, driver = driver, user = user, password = password).withSession {
        implicit session: Session =>

          def findById(id: String) = for {
            c <- referenceInfos if c.reference === id
          } yield c.customerName

          val rs = findById(info.reference).update("555")

          new Result[ReferenceInfo] (success = true, message = null, data = info)
      }
    }
  }

}
