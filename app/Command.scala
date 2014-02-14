import play.api.Play

// Read connection info from play configuration file.
// * Driver
// * Url
// * User
// * Password
object X {


  def run() = {
    val driver = Play.current.configuration.getString("db.default.driver").get
    val url = Play.current.configuration.getString("db.default.url").get
    val user = Play.current.configuration.getString("db.default.user").get
    val password = Play.current.configuration.getString("db.default.password").get

    println(s"driver: $driver")
    println(s"url: $url")
    println(s"user: $user")
    println(s"password: $password")

    // import scala.slick.driver.MySQLDriver
    scala.slick.model.codegen.SourceCodeGenerator.main(
      Array("scala.slick.driver.MySQLDriver", driver, url, "/home/recovery/projects/HelloSlick/generate", "com.ko.models")
    )
  }

  def main(args: Array[String]) = {
    run()
  }
}



