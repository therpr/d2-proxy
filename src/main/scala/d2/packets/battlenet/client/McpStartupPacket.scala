package d2.packets.battlenet.client

import java.nio.ByteBuffer

import akka.util.ByteString
import d2.packets.Packet

// https://bnetdocs.org/packet/320/mcp-startup
case class McpStartupPacket(id: Int, size: Int, data: Array[Byte]) extends Packet {
  override def toByteString = {
    val bb = ByteBuffer.allocate(1 + data.length)
    bb.put(id.toByte)
    bb.put(data)

    ByteString(bb.array())
  }
}