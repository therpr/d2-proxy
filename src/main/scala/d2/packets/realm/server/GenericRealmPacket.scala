package d2.packets.realm.server

import akka.util.ByteString
import d2.packets.Packet

case class GenericRealmPacket(id: Int, size: Int, data: Array[Byte], bs: ByteString) extends Packet {
  override def toByteString = bs
}
