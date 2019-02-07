package d2
import java.net.InetSocketAddress

import akka.actor.{ActorRef, Props}
import akka.io.Tcp.{Bind, Write}
import akka.io.{IO, Tcp}
import d2.ProxySupervisor.RealmClient2Server
import d2.RealmClientProxy.RealmClientClosed
import d2.packets.realm.client.RealmClientPacketBuilder
import d2.packets.{Packet, PacketBuilder}

object RealmClientProxy {
  def props(address: InetSocketAddress, supervisor: ActorRef) = Props(classOf[RealmClientProxy], address, supervisor)

  case class StartRealmClientProxy(address: InetSocketAddress)
  case object RealmClientClosed
}

class RealmClientProxy(address: InetSocketAddress, supervisor: ActorRef) extends TcpProxyActor {
  override val packetBuilder: PacketBuilder = RealmClientPacketBuilder

  import context._
  IO(Tcp) ! Bind(self, address)

  override def onReceivedData(data: Packet, sender: ActorRef): Unit = data match {
    case packet =>
      supervisor ! RealmClient2Server(packet)
  }

  override def onReceivedDataInternal(data: Packet, sender: ActorRef): Unit = remote ! Write(data.toByteString)

  override def postStop(): Unit = supervisor ! RealmClientClosed
}
