package d2
import java.net.InetSocketAddress

import akka.actor.{ActorRef, Props}
import akka.io.Tcp.{Connect, Write}
import akka.io.{IO, Tcp}
import d2.GameClientProxy.GameClientClosed
import d2.ProxySupervisor.GameServer2Client
import d2.packets.game.server.GameServerPacketBuilder
import d2.packets.{Packet, PacketBuilder}

object GameServerProxy {
  def props(remote: InetSocketAddress, supervisor: ActorRef) = Props(classOf[GameServerProxy], remote, supervisor)

  case class StartGameServerProxy(remote: InetSocketAddress)
}

class GameServerProxy(remoteAddr: InetSocketAddress, supervisor: ActorRef) extends TcpProxyActor {
  override val packetBuilder: PacketBuilder = GameServerPacketBuilder
  import context._

  connecting = true
  info(s"connecting to $remoteAddr")
  IO(Tcp) ! Connect(remoteAddr, Some(new InetSocketAddress("192.168.1.4", 4001)))

  override def onReceivedData(data: Packet, sender: ActorRef): Unit =  data match {
    case packet if packet.id == 0xAF =>
      info(s"received server logon challenge")

      supervisor ! GameServer2Client(packet)
    case packet =>
      info(s"received packet from server ${packet.hexId} - ${packet.size}")

      supervisor ! GameServer2Client(packet)
  }

  override def onReceivedDataInternal(data: Packet, sender: ActorRef): Unit = {
    remote ! Write(data.toByteString)
  }

  override def postStop(): Unit = supervisor ! GameClientClosed
}
