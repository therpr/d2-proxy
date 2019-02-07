package d2.packets

import akka.util.ByteString

trait Packet {
  val id: Int
  val size: Int
  val data: Array[Byte]
  val hexId: String = s"0x${id.toHexString}"

  override def toString: String = toByteString.toByteBuffer.array().map(x => s"0x${(x & 0xFF).toHexString}").mkString(" ")

  def toByteString: ByteString
}
