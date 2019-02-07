package d2.packets.game.server

import akka.util.ByteString
import d2.compression.Decompression
import d2.packets.{Packet, PacketBuilder}

import scala.collection.mutable.ArrayBuffer

object GameServerPacketBuilder extends PacketBuilder {
  private val packetBuffer = ArrayBuffer.empty[Byte]

  override def apply(bs: ByteString, packetNumber: Int): Option[Packet] = {
    if (packetNumber == 1) {
      val id = bs(0) & 0xFF
      val size = bs(1) & 0xFF
      val data = bs.toByteBuffer.array()

      return Some(GenericGameServerPacket(id, size, data, bs))
    }

    apply(bs)
  }

  override def apply(bs: ByteString): Option[Packet] = {
    packetBuffer.appendAll(bs.toByteBuffer.array)
    val packetBufferArray = packetBuffer.toArray

    val packetSize = Decompression.getPacketSize(packetBufferArray)
    if (packetSize > packetBuffer.length) return None

    /**
      * @todo handle segmentation of decompressed packet
      */
    val placeholder = Some(GenericGameServerPacket(0, 0, Array.empty, ByteString.fromArray(packetBufferArray)))
    val decompressedPackets: ArrayBuffer[Char] = ArrayBuffer.empty[Char]
    val decompressedPacketSize = Decompression.decompress(packetBufferArray, decompressedPackets)

    if(decompressedPacketSize == 0) {
      return placeholder
    }

    placeholder
  }
}
