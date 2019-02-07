package d2.packets.battlenet.server

import akka.util.ByteString
import d2.packets.Packet
import org.scalatest.FlatSpec

class RealmLogonPacketTest extends FlatSpec {
  private val bs: ByteString = Packet.toBS(Array(-1, 62, 81, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -28, 15, 0, 0, -58, 98, 54, 85, 23, -31, 0, 0, -20, -10, 99, 93, 0, 0, 0, 0, 0, 0, 0, 0, 80, 88, 50, 68, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 47, 65, -76, 108, 80, 60, 28, 15, -96, -109, 91, -53, 21, 0, -98, -65, -112, 80, 47, 109, 111, 98, 98, 0))
  private val packet = BattleNetServerPacketBuilder(bs).asInstanceOf[RealmLogonPacket]

  it should "parse ip addr from 0x3e packet" in {
    val ip = packet.ip

    assertResult("198.98.54.85")(ip)
  }

  it should "parse port from 0x3e packet" in {
    val port = packet.port

    assertResult(6113)(port)
  }

  it should "patch ip and port" in {
    val newIp = "127.0.0.1"
    val newPort: Short = 6666
    val logon = packet.patch(newIp, newPort)

    assertResult("127.0.0.1")(logon.ip)
    assertResult(6666)(logon.port)
  }
}

