package d2.packets.realm.client

import akka.util.ByteString
import d2.packets.Packet

case class GenericRealmClientPacket(id: Int, size: Int, data: Array[Byte], bs: ByteString) extends Packet {
  override def toByteString = bs
}
