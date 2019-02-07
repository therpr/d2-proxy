package d2.packets.game.client

import akka.util.ByteString
import d2.packets.{Packet, PacketBuilder}

object GameClientPacketBuilder extends PacketBuilder {
  override def apply(bs: ByteString): Option[Packet] = {
    val id = bs(0) & 0xFF

    if(bs.size < 2) {
      return Some(GenericGameClientPacket(id, 1, Array.empty, bs))
    }

    val size = bs(1) & 0xFF
    val data = bs.toByteBuffer.array()

    Some(GenericGameClientPacket(id, size, data, bs))
  }
}
