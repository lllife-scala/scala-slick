package controllers

import play.api.mvc._
import play.api.libs.iteratee.{Concurrent, Iteratee}
import play.api.libs.concurrent.Execution.Implicits._

object Realtime extends Controller {

  lazy val (enumerator, chanel) = Concurrent.broadcast[String]

  // Broadcast function
  def broadcastMessage(msg: String): Unit = {
    chanel.push(msg)
  }

  // Create hanlder
  // Broadcast message to all client
  def index = WebSocket.using[String] {
    request => {
      // Broadcast
      val iteratee = Iteratee.foreach[String] {
        msg =>
          broadcastMessage(msg)
      }
      (iteratee, enumerator)
    }
  }
}

