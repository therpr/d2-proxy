package d2.packets.realm.client

import akka.util.ByteString
import d2.packets.PacketBuilder

object RealmClientPacketBuilder extends PacketBuilder {
  override def apply(bs: ByteString): GenericRealmClientPacket = {
    if(bs.size < 2) {
      return GenericRealmClientPacket(bs(0) & 0xFF, 1, Array.empty, bs)
    }

    val id = bs(0) & 0xFF
    val size = bs(1) & 0xFF
    val data = bs.toByteBuffer.array()

    GenericRealmClientPacket(id, size, data, bs)
  }
}
