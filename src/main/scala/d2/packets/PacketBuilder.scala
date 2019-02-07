package d2.packets

import akka.util.ByteString

trait PacketBuilder {
  def apply(data: ByteString, packetNumber: Int): Option[Packet] = apply(data)
  def apply(data: ByteString): Option[Packet]
}
