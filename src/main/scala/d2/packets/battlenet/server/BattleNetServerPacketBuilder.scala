package d2.packets.battlenet.server

import akka.util.ByteString
import d2.packets.{Packet, PacketBuilder}
import grizzled.slf4j.Logging

object BattleNetServerPacketBuilder extends PacketBuilder with Logging {
  override def apply(data: ByteString): Option[Packet] = {
    if ((data(0) & 0xFF) != 255) {
      error(s"unknown packet ${data(0) & 0xFF}")

      val bs = data

      return Some(new Packet {
        override def toByteString: ByteString = bs
        override val data: Array[Byte] = Array.empty
        override val size: Int = 1
        override val id: Int = bs(0) & 0xFF
      })
    }

    data(1) & 0xFF match {
      case 0x3e =>
        Some(RealmLogonPacket(0x3e, data(2) & 0xFF, data.toByteBuffer.array.drop(3)))
      case id =>
        Some(GenericBattleNetServerPacket(id, data(2) & 0xFF, data.toByteBuffer.array.drop(3)))
    }
  }
}
