package d2

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import d2.packets.{Packet, PacketBuilder}
import grizzled.slf4j.Logging

import scala.collection.mutable

trait TcpProxyActor extends Actor with Logging {
  import context._

  val packetBuilder: PacketBuilder
  protected val onConnectionRegistered: mutable.Queue[Packet] = collection.mutable.Queue.empty[Packet]

  protected var remote: ActorRef = null
  private var socket: ActorRef = null
  protected val keepAlive = false
  protected var connecting = false
  protected var closed = false

  override def receive = {
    case b@Bound(localAddress) =>
      info(s"bound to ${localAddress.toString}; waiting for connection")
      socket = sender()
      onConnectionBound(localAddress)
    case CommandFailed(x: Bind) =>
      error(s"command failed: ${x.failureMessage.toString()}")
      IO(Tcp) ! Unbind
    case c@Connected(re, local) =>
      remote = sender()
      info(s"${local.getPort} connected to ${re.toString}")
      onConnected()
      sender() ! Register(getConnectionHandler)
      onConnectionRegistered.foreach(packet => sender() ! Write(packet.toByteString))
      connecting = false
    case PeerClosed =>
      closed = true // info for all loops

      if(remote == null) {
        context stop self
      } else if(!keepAlive)  {
        info("peer closed")
        if(socket == null)
          remote ! Abort // Client
        else
          socket ! Unbind // Server
      } else {
        info("peer closed but keepAlive is true")
      }
    case Unbound =>
      context stop self
    case Aborted =>
      context stop self
    case Received(data) => onReceivedData(packetBuilder(data), sender())
    case data: Packet => onReceivedDataInternal(data, sender())
  }

  def onConnectionBound(localAddress: InetSocketAddress) = {}

  def getConnectionHandler: ActorRef = self

  def onReceivedData(data: Packet, sender: ActorRef)

  def onReceivedDataInternal(data: Packet, sender: ActorRef)

  def onConnected() = {}
}