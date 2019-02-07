package d2.packets

import akka.util.ByteString

trait PacketBuilder {
  def apply(data: ByteString): Packet
}
