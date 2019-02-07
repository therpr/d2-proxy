package d2.packets.battlenet.server

import java.nio.ByteBuffer

import akka.util.ByteString
import d2.binary.BinaryShort
import d2.packets.Packet

case class RealmLogonPacket(id: Int, size: Int, data: Array[Byte]) extends Packet {
  var ip: String = data.slice(17, 21).map(_ & 0xFF).mkString(".")
  var port: Short = ByteBuffer.wrap(data).getShort(21)

  def patch(newIp: String, newPort: Short): RealmLogonPacket = {
    val newBytes = ByteBuffer.wrap(data)

    newBytes.position(17)
    val newIpBytes = newIp.split('.').map(_.toByte)
    newBytes.put(newIpBytes)
    newBytes.put(BinaryShort(newPort))

    val patched = this.copy(id, size, newBytes.array)

    patched
  }

  override def toByteString = {
    val bb = ByteBuffer.allocate(3 + data.length)
    bb.put(0xFF.toByte)
    bb.put(id.toByte)
    bb.put(size.toByte)
    bb.put(data)

    ByteString(bb.array())
  }
}
