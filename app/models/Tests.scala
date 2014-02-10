package models

import scala.slick.driver.MySQLDriver.simple._
import java.sql.Date

//import scala.slick.driver.H2Driver._

/*
	`ID` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	`Username` VARCHAR(45) NOT NULL,
	`Password` VARCHAR(45) NOT NULL,
	`Firstname` VARCHAR(45) NOT NULL,
	`Lastname` VARCHAR(45) NOT NULL,
	`Position` VARCHAR(45) NOT NULL,
	`IsDelete` TINYINT(1) NOT NULL,
	`CreateDate` DATETIME NOT NULL,
	PRIMARY KEY (`ID`)
 */

//case class UserInfo()

object Tests{

  // Default constructor
  case class UserInfo( var id: Int,
                       userName: String,
                       password: String,
                       firstName: String,
                       lastName: String,
                       position: String,
                       isDelete: Boolean,
                       createDate : Date ) {

    // Minimalize
    def this() = this(0, "","","","","", false, new Date(new java.util.Date().getTime()))
  }

  // UserInfos = "buserinfo"
  class UserInfos(tag: Tag) extends Table[UserInfo](tag, "buserinfo") {

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def userName = column[String]("Username")

    def password = column[String]("Password")

    def firstName = column[String]("Firstname")

    def lastName = column[String]("Lastname")

    def position = column[String]("Position")

    def isDelete = column[Boolean]("IsDelete")

    def createDate = column[Date]("CreateDate")

    def * = (id, userName, password, firstName, lastName, position, isDelete, createDate) <>(UserInfo.tupled, UserInfo.unapply)
  }

  val users = TableQuery[UserInfos]
  val user = new UserInfo()
  user.id = 77

  Database.forURL("jdbc:mysql://10.0.0.114:3306/bupa2014", driver="com.mysql.jdbc.Driver", user="root", password = "konetwork" ).withSession {
    implicit session: Session =>
      users.insert(user)
  }
}

