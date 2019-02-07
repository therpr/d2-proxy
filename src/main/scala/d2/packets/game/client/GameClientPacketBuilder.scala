package d2.packets.game.client

import akka.util.ByteString
import d2.packets.PacketBuilder

object GameClientPacketBuilder extends PacketBuilder {
  override def apply(bs: ByteString): GenericGameClientPacket = {
    val id = bs(0) & 0xFF

    if(bs.size < 2) {
      return GenericGameClientPacket(id, 1, Array.empty, bs)
    }

    val size = bs(1) & 0xFF
    val data = bs.toByteBuffer.array()

    GenericGameClientPacket(id, size, data, bs)
  }
}
