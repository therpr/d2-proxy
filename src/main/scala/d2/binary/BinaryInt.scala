package d2.binary

import java.nio.ByteBuffer

object BinaryInt {
  def apply(bytes: Array[Byte]): Int =
    ByteBuffer.wrap(bytes).getInt
}
