package d2.compression

import org.scalatest.FlatSpec

class DecompressionTest extends FlatSpec {

  it should "return packet size equal to first byte when first byte is < 240" in {
    val packet1 = Array(239.toByte)
    assert(Decompression.getPacketSize(packet1) == 239)

    val packet2 = Array(33.toByte)
    assert(Decompression.getPacketSize(packet2) == 33)
  }

  it should "compute packet size based on two first bytes when first byte is >= 240" in {
    val packet1 = Array(240.toByte, 1.toByte)
    assert(Decompression.getPacketSize(packet1) == 1)

    val packet2 = Array(241.toByte, 1.toByte)
    assert(Decompression.getPacketSize(packet2) == 257)

    val packet3 = Array(241.toByte, 255.toByte)
    assert(Decompression.getPacketSize(packet3) == 511)
  }
}
