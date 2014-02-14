package service

import org.java_websocket.client.WebSocketClient
import java.net.URI
import org.java_websocket.drafts.Draft_76
import org.java_websocket.handshake.ServerHandshake
import play.api.Play

object SocketService {

  // Model variable
  // * service.socket.uri from conf/application.cinf
  // * draf_76 from user manual
  // * default play logger
  private val config = Play.current.configuration.getString("service.socket.uri").get
  private val uri = new URI(config)
  private val draf = new Draft_76()
  private val logger = play.api.Logger

  // Broadcast string to all client
  // Use inline implementation of WebSocketClient
  // Send message within onOpen method
  def send(message: String) = {

    // Inline implementation
    new WebSocketClient(uri, draf) {
      def onError(ex: Exception): Unit = {
        logger.error("Socket Error: " + ex.getMessage)
      }

      def onMessage(message: String): Unit = {
        logger.info("Socket Message: " + message)
      }

      def onClose(code: Int, reason: String, remote: Boolean): Unit = {
        logger.info("Socket Close: " + reason)
      }

      def onOpen(handshakedata: ServerHandshake): Unit = {
        logger.info("Socket Open: " + handshakedata.getHttpStatus)
        send(message)
      }
    }.connect()
  }
}
