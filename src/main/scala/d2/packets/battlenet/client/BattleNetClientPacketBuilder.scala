package d2.packets.battlenet.client

import akka.util.ByteString
import d2.packets.{Packet, PacketBuilder}

object BattleNetClientPacketBuilder extends PacketBuilder{
  override def apply(data: ByteString): Option[Packet] = {
    data(0) & 0xFF match {
      case 0x01 =>
        Some(McpStartupPacket(0x01, 1, data.toByteBuffer.array.drop(1)))
      case id =>
        Some(GenericBattleNetClientPacket(id, 1, data.toByteBuffer.array.drop(1), data))
    }
  }
}
