package d2.packets.realm.server

import akka.util.ByteString
import d2.packets.{Packet, PacketBuilder}

object RealmPacketBuilder extends PacketBuilder {
  override def apply(data: ByteString): Packet = {
    val x  = data.toByteBuffer.array().map(_ & 0xFF)

    if(data.size < 3) {
      return GenericRealmPacket(0, 0, Array.empty, data)
    }

    data(2) & 0xFF match {
      case 0x04 =>
        GameServerLogonResponsePacket(0x04, data(0) & 0xFF, data(1) & 0xFF, data.toByteBuffer.array.drop(3))
      case id =>
        GenericRealmPacket(id, data(0) & 0xFF, data.toByteBuffer.array.drop(3), data)
    }
  }
}
