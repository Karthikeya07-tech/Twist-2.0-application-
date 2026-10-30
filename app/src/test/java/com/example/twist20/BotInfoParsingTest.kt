package com.example.twist20

import com.example.twist20.data.BotInfo
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class BotInfoParsingTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun decodesUnprovisionedBoard() {
        val info = json.decodeFromString(
            BotInfo.serializer(),
            """
            {"protocol":1,"name":"Twist R&D","type":"Twist","stage":"rd","type_id":3,"firmware":"0.4.4","serial":"","provisioned":false,"hw_rev":"","mac":"E8:F6:0A:E8:CF:32","ssid":"TWIST","ip":"192.168.4.1","ctrl_port":7780}
            """.trimIndent(),
        )

        assertEquals("Twist R&D", info.displayName)
        assertEquals("Twist", info.type)
        assertEquals("R&D", info.stageLabel)
        assertEquals("0.4.4", info.firmware)
        assertEquals("E8:F6:0A:E8:CF:32", info.mac)
        assertEquals("TWIST", info.ssid)
        assertEquals("192.168.4.1", info.ip)
        assertEquals(7780, info.controlPortOrDefault)
        assertFalse(info.provisioned)
    }
}
