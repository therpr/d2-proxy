package d2.binary

object UnsignedByte {
  def apply(byte: Byte): Int = byte & 0xFF
}
