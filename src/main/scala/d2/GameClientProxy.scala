package d2
import java.net.InetSocketAddress

import akka.actor.{ActorRef, Props}
import akka.io.Tcp.{Bind, Write}
import akka.io.{IO, Tcp}
import d2.GameClientProxy.GameClientClosed
import d2.GameServerProxy.StartGameServerProxy
import d2.ProxySupervisor.GameClient2Server
import d2.packets.game.client.GameClientPacketBuilder
import d2.packets.{Packet, PacketBuilder}

object GameClientProxy {
  def props(local: InetSocketAddress, remote: InetSocketAddress, supervisor: ActorRef) = Props(classOf[GameClientProxy], local, remote, supervisor)

  case class StartGameClientProxy(local: InetSocketAddress, remote: InetSocketAddress)
  case object GameClientClosed
}

class GameClientProxy(localAddr: InetSocketAddress, remoteAddr: InetSocketAddress, supervisor: ActorRef) extends TcpProxyActor {
  override val packetBuilder: PacketBuilder = GameClientPacketBuilder

  import context._
  IO(Tcp) ! Bind(self, localAddr)

  private var logonChallengeReceivedFromServer = false
  private var logonInfoSent = false
  private var logonInfo: Packet = null

  override def onReceivedData(data: Packet, sender: ActorRef): Unit = data match {
    case packet if packet.id == 0x6D =>
      info(s"game client PING")

      supervisor ! GameClient2Server(packet, remoteAddr)
    case packet if packet.id == 0x68 && logonChallengeReceivedFromServer =>
      info(s"game logon info received")
      logonInfoSent = true

      supervisor ! GameClient2Server(packet, remoteAddr)
    case packet if packet.id == 0x68 =>
      info(s"game logon info received but waiting for logon challenge from server")
      logonInfo = packet
    case packet =>
      info(s"received packet from client ${packet.hexId} - ${packet.toByteString.toByteBuffer.array().map(_ & 0xFF).map(x => s"0x${x.toHexString}").mkString(" ")}")
      supervisor ! GameClient2Server(packet, remoteAddr)
  }

  override def onReceivedDataInternal(data: Packet, sender: ActorRef): Unit = data match {
    case packet if packet.id == 0xAF =>
      logonChallengeReceivedFromServer = true

      if(logonInfo != null) {
        info(s"received logon challenge, but logon info was already sent, faking client response")

        onReceivedData(logonInfo, remote)
      } else {
        info(s"received logon challenge passing to client")

        remote ! Write(packet.toByteString)
      }
    case packet if packet.id == 0x08 && packet.size == 16 =>
      info(s"probably received check packet ($packet); sending 0x2b response")

      remote ! Write(packet.toByteString)
    case packet =>
      info(s"received from game: $packet")
      remote ! Write(packet.toByteString)
  }

  override def postStop(): Unit = supervisor ! GameClientClosed

  override def onConnected(): Unit = supervisor ! StartGameServerProxy(remoteAddr)
}
