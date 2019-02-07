package d2.packets.realm.server

import java.nio.ByteBuffer

import akka.util.ByteString
import d2.binary.BinaryInt
import d2.packets.Packet

case class GameServerLogonResponsePacket(id: Int, size: Int, unknownByte: Int, data: Array[Byte]) extends Packet {
  val RESULT_SUCCESS = 0x00
  val RESULT_PASSWORD_INCORRECT = 0x29
  val RESULT_GAME_DOES_NOT_EXIST = 0x2A
  val RESULT_GAME_IS_FULL = 0x2B
  val RESULT_YOU_DO_NOT_MEET_THE_LEVEL_REQUIREMENTS_FOR_THIS_GAME = 0x2C
  val RESULT_DEAD_HARDCORE_CHARACTER_CANNOT_JOIN_A_GAME = 0x6E
  val RESULT_NON_HARDCORE_CHARACTER_CANNOT_JOIN_A_GAME_CREATED_BY_A_HARDCORE_CHARACTER = 0x71
  val RESULT_UNABLE_TO_JOIN_A_NIGHTMARE_GAME = 0x73
  val RESULT_UNABLE_TO_JOIN_A_HELL_GAME = 0x74
  val RESULT_NON_EXPANSION_CHARACTER_CANNOT_JOIN_A_GAME_CREATED_BY_AN_EXPANSION_CHARACTER = 0x78
  val RESULT_EXPANSION_CHARACTER_CANNOT_JOIN_A_GAME_CREATED_BY_A_NON_EXPANSION_CHARACTER = 0x79
  val RESULT_NON_LADDER_CHARACTER_CANNOT_JOIN_A_GAME_CREATED_BY_A_LADDER_CHARACTER = 0x7D

  val ip: String = data.slice(6, 10).map(_ & 0xFF).mkString(".")
  val result: Int = BinaryInt(data.drop(14))

  def patch(newIp: String): GameServerLogonResponsePacket = {
    val newBytes = ByteBuffer.wrap(data)

    newBytes.position(6)
    val newIpBytes = newIp.split('.').map(x => (x.toInt & 0xFF).toByte)
    newBytes.put(newIpBytes)

    val patched = this.copy(id, size, unknownByte, newBytes.array)

    patched
  }

  override def toByteString = {
    val bb = ByteBuffer.allocate(3 + data.length)
    bb.put(size.toByte)
    bb.put(unknownByte.toByte)
    bb.put(id.toByte)
    bb.put(data)

    ByteString(bb.array())
  }
}
