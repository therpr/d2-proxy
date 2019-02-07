package d2

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.io.Tcp.PeerClosed
import d2.BattleNetClientProxy.BattleNetClientClosed
import d2.GameClientProxy.{GameClientClosed, StartGameClientProxy}
import d2.GameServerProxy.StartGameServerProxy
import d2.ProxySupervisor._
import d2.RealmClientProxy.{RealmClientClosed, StartRealmClientProxy}
import d2.packets.Packet
import grizzled.slf4j.Logging

object ProxySupervisor {

  case class BattleNetClient2Server(packet: Packet)

  case class BattleNetServer2Client(packet: Packet)

  case class RealmClient2Server(packet: Packet)

  case class RealmServer2Client(packet: Packet)

  case class GameClient2Server(packet: Packet, remote: InetSocketAddress)

  case class GameServer2Client(packet: Packet)

}

class ProxySupervisor extends Actor with Logging {
  private var bnetClientProxy: Option[ActorRef] = None
  private var bnetServerProxy: Option[ActorRef] = None
  private var realmClientProxy: Option[ActorRef] = None
  private var realmServerProxy: Option[ActorRef] = None
  private var gameClientProxy: Option[ActorRef] = None
  private var gameServerProxy: Option[ActorRef] = None

  override def receive = {
    case "start" =>
      info("starting battle.net client proxy")
      getBattleNetClientProxy
    case StartRealmClientProxy(address) =>
      info("starting realm client proxy")
      createRealmClientProxy(address)
      // close all gs connections if any exist

      gameClientProxy match {
        case Some(proxy) =>
          proxy ! PeerClosed
          gameClientProxy = None
        case _ => ;
      }

      gameServerProxy match {
        case Some(proxy) =>
          proxy ! PeerClosed
          gameServerProxy = None
        case _ => ;
      }
    case StartGameClientProxy(local, remote) =>
      info(s"starting game client proxy")
      createGameClientProxy(local, remote)
    case StartGameServerProxy(remote) =>
      getGameServerProxy(remote)
    case GameClient2Server(packet, remote) =>
      getGameServerProxy(remote) ! packet
    case GameServer2Client(packet) =>
      getGameClientProxy ! packet
    case RealmClient2Server(packet) =>
      getRealmServerProxy ! packet
    case RealmServer2Client(packet) =>
      getRealmClientProxy ! packet
    case BattleNetClient2Server(packet) =>
      getBattleNetServerProxy ! packet
    case BattleNetServer2Client(packet) =>
      getBattleNetClientProxy ! packet
    case BattleNetClientClosed =>
      getBattleNetServerProxy ! PeerClosed

      bnetClientProxy = None
      bnetServerProxy = None

      self ! "start"
    case RealmClientClosed =>
      realmClientProxy = None

      realmServerProxy match {
        case Some(proxy) =>
          proxy ! PeerClosed
          realmServerProxy = None
        case _ => ;
      }
    case GameClientClosed =>
      gameClientProxy = None

      gameServerProxy match {
        case Some(proxy) =>
          proxy ! PeerClosed
          gameServerProxy = None
        case _ => ;
      }
  }

  def getBattleNetClientProxy: ActorRef = bnetClientProxy match {
    case Some(proxy) => proxy
    case None =>
      val proxy = context.actorOf(BattleNetClientProxy.props(6112, self))
      bnetClientProxy = Some(proxy)
      proxy
  }

  def getBattleNetServerProxy: ActorRef = bnetServerProxy match {
    case Some(proxy) => proxy
    case None =>
      val proxy = context.actorOf(BattleNetServerProxy.props(self))
      bnetServerProxy = Some(proxy)
      proxy
  }

  def createRealmClientProxy(address: InetSocketAddress): ActorRef = {
    val proxy = context.actorOf(RealmClientProxy.props(address, self))
    realmClientProxy = Some(proxy)
    proxy
  }

  def getRealmClientProxy: ActorRef = realmClientProxy match {
    case Some(proxy) => proxy
    case None => throw new Exception("realm client proxy was not created yet")
  }

  def getRealmServerProxy: ActorRef = realmServerProxy match {
    case Some(proxy) => proxy
    case None =>
      val proxy = context.actorOf(RealmServerProxy.props(self))
      realmServerProxy = Some(proxy)
      proxy
  }

  def createGameClientProxy(local: InetSocketAddress, remote: InetSocketAddress): ActorRef = {
    val proxy = context.actorOf(GameClientProxy.props(local, remote, self))
    gameClientProxy = Some(proxy)
    proxy
  }

  def getGameClientProxy: ActorRef = gameClientProxy match {
    case Some(proxy) => proxy
    case None => throw new Exception("game client proxy was not created yet")
  }

  def getGameServerProxy(remote: InetSocketAddress): ActorRef = gameServerProxy match {
    case Some(proxy) => proxy
    case None =>
      val proxy = context.actorOf(GameServerProxy.props(remote, self))
      gameServerProxy = Some(proxy)
      proxy
  }
}
