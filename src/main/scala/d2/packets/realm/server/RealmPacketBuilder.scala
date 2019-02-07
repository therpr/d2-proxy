package d2.packets.realm.server

import akka.util.ByteString
import d2.packets.{Packet, PacketBuilder}

object RealmPacketBuilder extends PacketBuilder {
  override def apply(data: ByteString): Option[Packet] = {
    val x  = data.toByteBuffer.array().map(_ & 0xFF)

    if(data.size < 3) {
      return Some(GenericRealmPacket(0, 0, Array.empty, data))
    }

    data(2) & 0xFF match {
      case 0x04 =>
        Some(GameServerLogonResponsePacket(0x04, data(0) & 0xFF, data(1) & 0xFF, data.toByteBuffer.array.drop(3)))
      case id =>
        Some(GenericRealmPacket(id, data(0) & 0xFF, data.toByteBuffer.array.drop(3), data))
    }
  }
}
