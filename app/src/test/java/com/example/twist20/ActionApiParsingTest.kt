package com.example.twist20

import com.example.twist20.data.ActionReply
import com.example.twist20.data.ActionsResponse
import com.example.twist20.data.humanizeAction
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ActionApiParsingTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun decodesActionCatalog() {
        val response = json.decodeFromString(
            ActionsResponse.serializer(),
            """{"actions":["angry","excited","fear","greet","happy","jump","neutral","turn_left","turn_right","walk_backward","walk_diagonal","walk_forward","yoga"]}""",
        )
        assertEquals(13, response.actions.size)
        assertTrue(response.actions.contains("greet"))
        assertTrue(response.actions.contains("walk_forward"))
    }

    @Test
    fun decodesPlayReply() {
        val reply = json.decodeFromString(
            ActionReply.serializer(),
            """{"ok":true,"action":"greet","state":2}""",
        )
        assertTrue(reply.ok)
        assertEquals("greet", reply.action)
        assertEquals(2, reply.state)
    }

    @Test
    fun decodesUnknownAction() {
        val reply = json.decodeFromString(
            ActionReply.serializer(),
            """{"ok":false,"error":"ESP_ERR_NOT_FOUND"}""",
        )
        assertEquals("ESP_ERR_NOT_FOUND", reply.error)
    }

    @Test
    fun humanizesFirmwareNames() {
        assertEquals("Walk Forward", "walk_forward".humanizeAction())
        assertEquals("Greet", "greet".humanizeAction())
        assertEquals("Turn Left", "turn_left".humanizeAction())
    }
}
