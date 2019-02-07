package d2.packets.game.server

import akka.util.ByteString
import d2.packets.PacketBuilder

object GameServerPacketBuilder extends PacketBuilder {
  override def apply(bs: ByteString): GenericGameServerPacket = {
    val id = bs(0) & 0xFF

    if(bs.size < 2) {
      return GenericGameServerPacket(id, 1, Array.empty, bs)
    }

    val size = bs(1) & 0xFF
    val data = bs.toByteBuffer.array()

    GenericGameServerPacket(id, size, data, bs)
  }
}
