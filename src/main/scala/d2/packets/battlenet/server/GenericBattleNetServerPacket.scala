package d2.packets.battlenet.server

import java.nio.ByteBuffer

import akka.util.ByteString
import d2.packets.Packet

case class GenericBattleNetServerPacket(id: Int, size: Int, data: Array[Byte]) extends Packet {
  override def toByteString = {
    val bb = ByteBuffer.allocate(3 + data.length)
    bb.put(0xFF.toByte)
    bb.put(id.toByte)
    bb.put(size.toByte)
    bb.put(data)

    ByteString(bb.array())
  }
}
