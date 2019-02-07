package d2.binary

object BinaryShort {
  def apply(number: Short): Array[Byte] =
    Array(((number >> 8) & 0xFF).toByte, (number & 0xFF).toByte)
}
