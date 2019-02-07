package d2.packets.realm.server

import ch.ethz.acl.passera.unsigned.UByte
import d2.packets.Packet

trait RealmPacket extends Packet {
  val data: Array[UByte] = uBytesArray.drop(2)
  val size: Int = uBytesArray(0).toUInt.intRep
  val id: Int = uBytesArray(2).toUInt.intRep
}
