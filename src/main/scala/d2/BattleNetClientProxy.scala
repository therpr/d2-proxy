package d2
import java.net.InetSocketAddress

import akka.actor.{ActorRef, Props}
import akka.io.Tcp.{Bind, Write}
import akka.io.{IO, Tcp}
import d2.BattleNetClientProxy.BattleNetClientClosed
import d2.ProxySupervisor.BattleNetClient2Server
import d2.packets.battlenet.client.BattleNetClientPacketBuilder
import d2.packets.{Packet, PacketBuilder}

object BattleNetClientProxy {
  def props(port: Short, supervisor: ActorRef) = Props(classOf[BattleNetClientProxy], port, supervisor)

  case object BattleNetClientClosed
}

class BattleNetClientProxy(port: Short, supervisor: ActorRef) extends TcpProxyActor {
  import context.system

  override val packetBuilder: PacketBuilder = BattleNetClientPacketBuilder

  IO(Tcp) ! Bind(self, new InetSocketAddress(port))

  override def onReceivedData(data: Packet, sender: ActorRef): Unit = data match {
    case packet =>
      debug(s"client sending packet id ${packet.id}")

      supervisor ! BattleNetClient2Server(packet)
  }

  override def onReceivedDataInternal(data: Packet, sender: ActorRef): Unit =  remote ! Write(data.toByteString)

  override def postStop(): Unit = supervisor ! BattleNetClientClosed
}
