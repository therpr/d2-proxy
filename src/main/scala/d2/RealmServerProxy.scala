package d2

import java.net.InetSocketAddress

import akka.actor.{ActorRef, Props}
import akka.io.Tcp.{Connect, Write}
import akka.io.{IO, Tcp}
import d2.GameClientProxy.StartGameClientProxy
import d2.ProxySupervisor.RealmServer2Client
import d2.packets.realm.server.{GameServerLogonResponsePacket, RealmPacketBuilder}
import d2.packets.{Packet, PacketBuilder}

object RealmServerProxy {
  def props(supervisor: ActorRef) = Props(classOf[RealmServerProxy], supervisor)
}

class RealmServerProxy(supervisor: ActorRef) extends TcpProxyActor {
  override val packetBuilder: PacketBuilder = RealmPacketBuilder

  import context._

  override def onReceivedData(data: Packet, sender: ActorRef): Unit = data match {
    case packet: GameServerLogonResponsePacket =>
      packet.result match {
        case packet.RESULT_SUCCESS =>
          val patched = packet.patch(Config.LOCAL_IP)
          info(s"patched game logon packet from ${packet.ip} to ${patched.ip}")

          supervisor ! StartGameClientProxy(new InetSocketAddress(Config.LOCAL_IP, 4000), new InetSocketAddress(packet.ip, 4000))
          supervisor ! RealmServer2Client(packet)
        case status =>
          info(s"game logon status failed id: $status")
      }
    case packet =>
      info(s"received packet ${packet.id}")
      supervisor ! RealmServer2Client(packet)
  }

  override def onReceivedDataInternal(data: Packet, sender: ActorRef): Unit = {
    if(remote == null) {
      onConnectionRegistered.enqueue(data)
      IO(Tcp) ! Connect(new InetSocketAddress(Config.SERVER_IP, 6113))

      return
    }

    remote ! Write(data.toByteString)
  }
}