package d2.packets.game.server

import akka.util.ByteString
import d2.packets.Packet

case class GenericGameServerPacket(id: Int, size: Int, data: Array[Byte], bs: ByteString) extends Packet {
  override def toByteString = bs
}
