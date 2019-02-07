package d2

import java.net.InetSocketAddress

import akka.actor.{ActorRef, Props}
import akka.io.Tcp.{Connect, Write}
import akka.io.{IO, Tcp}
import d2.ProxySupervisor.BattleNetServer2Client
import d2.RealmClientProxy.StartRealmClientProxy
import d2.packets.battlenet.client.McpStartupPacket
import d2.packets.battlenet.server.{BattleNetServerPacketBuilder, RealmLogonPacket}
import d2.packets.{Packet, PacketBuilder}

object BattleNetServerProxy {
  def props(supervisor: ActorRef) = Props(classOf[BattleNetServerProxy], supervisor)
}

class BattleNetServerProxy(supervisor: ActorRef) extends TcpProxyActor {

  import context.system

  override val packetBuilder: PacketBuilder = BattleNetServerPacketBuilder

  override def onReceivedData(data: Packet, sender: ActorRef): Unit = data match {
    case packet: RealmLogonPacket =>
      val patched = packet.patch("127.0.0.1", 6666)
      info(s"patched realm logon from ${packet.ip}:${packet.port} to ${patched.ip}:${patched.port}")

      supervisor ! StartRealmClientProxy(new InetSocketAddress("127.0.0.1", 6666))
      supervisor ! BattleNetServer2Client(packet)
    case packet =>
      info(s"received from server: $packet")

      supervisor ! BattleNetServer2Client(packet)
  }

  override def onReceivedDataInternal(data: Packet, sender: ActorRef): Unit = data match {
    case packet: McpStartupPacket =>
      info(s"client requested mcp startup") // run server

      if (remote == null) {
        onConnectionRegistered.enqueue(packet)
        IO(Tcp) ! Connect(new InetSocketAddress(Config.BATTLE_NET_GATEWAY, 6112))

        return
      }

      remote ! Write(packet.toByteString)
    case packet =>
//      info(s"client sent unknown packet ${packet.hexId}")
      remote ! Write(packet.toByteString)
  }
}
