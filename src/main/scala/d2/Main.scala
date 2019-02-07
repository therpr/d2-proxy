package d2

import akka.actor.Props

object Main extends App {
  import akka.actor.ActorSystem

  val system = ActorSystem.create("ServerActorSystem")

  system.actorOf(Props(classOf[ProxySupervisor])) ! "start"
}