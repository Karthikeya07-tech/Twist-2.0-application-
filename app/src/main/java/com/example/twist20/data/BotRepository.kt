package com.example.twist20.data

import android.content.Context
import android.net.Network
import com.example.twist20.network.LocalWifi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class BotRepository(context: Context) {
    private val appContext = context.applicationContext
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchInfo(): BotInfo = withWifi { network ->
        val (_, body) = execute(network, Request.Builder().url(infoUrl()).get().build())
        decode(body, BotInfo.serializer())
    }

    suspend fun fetchActions(): List<String> = withWifi { network ->
        val (_, body) = execute(network, Request.Builder().url(actionsUrl()).get().build())
        decode(body, ActionsResponse.serializer()).actions
    }

    suspend fun playAction(name: String): ActionReply = withWifi { network ->
        val payload = json.encodeToString(ActionRequest.serializer(), ActionRequest(name))
        val request = Request.Builder()
            .url(actionUrl())
            .post(payload.toRequestBody(JSON))
            .header("Content-Type", "application/json")
            .build()
        val (code, body) = execute(network, request, allowNotFound = true)
        val reply = runCatching { decode(body, ActionReply.serializer()) }
            .getOrElse { throw BadBotResponseException(it) }
        when {
            code == 404 || (!reply.ok && reply.error == "ESP_ERR_NOT_FOUND") ->
                throw UnknownActionException(reply.error)
            code !in 200..299 || !reply.ok ->
                throw ActionFailedException(reply.error)
            else -> reply
        }
    }

    suspend fun stop(): ActionReply = withWifi { network ->
        val request = Request.Builder()
            .url(stopUrl())
            .post("{}".toRequestBody(JSON))
            .header("Content-Type", "application/json")
            .build()
        val (_, body) = execute(network, request)
        val reply = decode(body, ActionReply.serializer())
        if (!reply.ok) throw ActionFailedException(reply.error)
        reply
    }

    private suspend fun <T> withWifi(block: (Network?) -> T): T = withContext(Dispatchers.IO) {
        val wifi = LocalWifi.network(appContext)
        try {
            block(wifi)
        } catch (mapped: BotApiException) {
            throw mapped
        } catch (first: Exception) {
            if (wifi != null) {
                try {
                    block(null)
                } catch (mapped: BotApiException) {
                    throw mapped
                } catch (_: Exception) {
                    throw mapError(first)
                }
            } else {
                throw mapError(first)
            }
        }
    }

    private fun execute(
        network: Network?,
        request: Request,
        allowNotFound: Boolean = false,
    ): Pair<Int, String> {
        newClient(network).newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful && !(allowNotFound && response.code == 404)) {
                throw IOException("HTTP ${response.code}")
            }
            return response.code to body
        }
    }

    private fun <T> decode(body: String, serializer: kotlinx.serialization.DeserializationStrategy<T>): T {
        return try {
            json.decodeFromString(serializer, body)
        } catch (parse: Exception) {
            throw BadBotResponseException(parse)
        }
    }

    private fun newClient(network: Network?): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(4, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .callTimeout(12, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .apply {
                if (network != null) socketFactory(network.socketFactory)
            }
            .build()

    private fun mapError(error: Exception): Exception = when (error) {
        is BotApiException -> error
        is UnknownHostException,
        is SocketTimeoutException,
        is ConnectException,
        is IOException -> UnreachableBotException(error)
        else -> UnreachableBotException(error)
    }

    companion object {
        private val JSON = "application/json; charset=utf-8".toMediaType()
        private const val BASE = "http://192.168.4.1:1800"
        fun infoUrl() = "$BASE/info"
        fun actionsUrl() = "$BASE/actions"
        fun actionUrl() = "$BASE/action"
        fun stopUrl() = "$BASE/stop"
    }
}

sealed class BotApiException(cause: Throwable? = null) : IOException(cause)
class UnreachableBotException(cause: Throwable) : BotApiException(cause)
class BadBotResponseException(cause: Throwable) : BotApiException(cause)
class UnknownActionException(val serverError: String?) : BotApiException()
class ActionFailedException(val serverError: String?) : BotApiException()
