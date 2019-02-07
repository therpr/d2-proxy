package d2.compression

import grizzled.slf4j.Logging

import scala.collection.mutable.ArrayBuffer

object Decompression extends Logging {
  /**
    * @see http://bitwisecmd.com/
    */
  def getPacketSize(bytes: Array[Byte]): Int = {
    val unsigned = bytes.map(_.unsigned)

    if (getHeaderSize(bytes) == 1) return unsigned(0)

    /**
      * for example:
      * 0x01 & 0xF (0 0 0 0 0 0 0 1 & 0 0 0 0 1 1 1 1) = 0 0 0 1 = 1
      */
    val firstByte = unsigned(0) & 0xF
    /**
      * for example:
      * 0x01 (0 0 0 0 0 0 0 1) << 8 = 1 0 0 0 0 0 0 0 (0x100 = 256)
      */
    val shift8PositionsToLeft = firstByte << 8

    shift8PositionsToLeft + unsigned(1)
  }

  // 240 = 0xF0 = 1 1 1 1 0 0 0 0
  def getHeaderSize(bytes: Array[Byte]): Int = if (bytes(0).unsigned < 240) 1 else 2

  def decompress(compressed: Array[Byte], out: ArrayBuffer[Char]): Int = {
    val compressedPacketLength = getPacketSize(compressed)

    if(compressedPacketLength == 0) return 0

    val decompressedPacketLength = decompressPacketData(compressed, out)

    decompressedPacketLength
  }

  def decompressPacketData(compressed: Array[Byte], out: ArrayBuffer[Char]): Int = {
    var size: Int = getPacketSize(compressed) - getHeaderSize(compressed)
    var a, b, c, d = 0
    var cnt = 32
    var x = getHeaderSize(compressed)
    var index = 0
    val outLength = out.length
    var maxCount = outLength

    while (true) {
      if (cnt >= 8) {
        while (size > 0 && cnt >= 8) {
          cnt -= 8
          size -= 1
          a = compressed(x).unsigned << cnt
          x += 1
          b = b | a
        }
      }

      index = charIndex(b >> 0x18)
      a = charTable(index)
      d = (b >> (0x18 - a)) & bitMasks(a)
      c = charTable(index + 2 * d + 2)

      cnt += c
      if (cnt > 0x20) return outLength - maxCount

      if (maxCount - 1 == 0) return -1

      maxCount -= 1
      a = charTable(index + (2 * d) + 1)
      out.append(a.toChar)

      b <<= (c & 0xFF)
    }

    -1
  }
}
