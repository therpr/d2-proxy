package d2.packets.battlenet.client

import java.nio.ByteBuffer

import akka.util.ByteString
import d2.packets.Packet

case class GenericBattleNetClientPacket(id: Int, size: Int, data: Array[Byte], byteString: ByteString) extends Packet {
  override def toByteString = {
    val bb = ByteBuffer.allocate(1 + data.length)
    bb.put(id.toByte)
    bb.put(data)

    ByteString(bb.array())
  }
}
