package com.github.catomon.moemarathon.leaderboard

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter

object LeaderboardService {
    fun fetchLeaderboard(onResult: OnResult) {
        val httpRequest = Net.HttpRequest("GET").apply {
            url = "http://dreamlo.com/lb/67daf22a8f40bbc22497e381/json"
        }

        Gdx.net.sendHttpRequest(httpRequest, ResponseListener(onResult))
    }

    private fun parseLeaderboard(jsonString: String): DreamloLeaderboard? {
        val json = Json().apply {
            ignoreUnknownFields = true
        }

        try {
            val jsonValue = JsonReader().parse(jsonString)
            //cuz of if dreamlo has 1 item it puts an object not an array for some reason
            if (jsonValue?.get("dreamlo")?.get("leaderboard")?.get("entry")?.isArray == false) {
                val old = jsonValue.get("dreamlo").get("leaderboard").get("entry")
                old.remove()
                jsonValue.get("dreamlo").get("leaderboard").addChild("entry", JsonValue(JsonValue.ValueType.array))
                jsonValue.get("dreamlo").get("leaderboard").get("entry").addChild(old)
            }
            val leaderboard =
                json.fromJson(DreamloLeaderboard::class.java, jsonValue.toJson(JsonWriter.OutputType.json))
            return leaderboard
        } catch (e: Exception) {
            Gdx.app.error("JSON", "Parsing failed", e)
            return null
        }
    }

    fun submitScore(modeName: String, playerName: String, score: Int, rank: String) {
        val url = "http://dreamlo.com/lb/$key/add/$playerName/$score/-1/${modeName + "_" + rank}"
        val httpRequest = Net.HttpRequest(Net.HttpMethods.GET).apply {
            this.url = url
        }

        Gdx.net.sendHttpRequest(httpRequest, ResponseListener2())
    }

    private class ResponseListener2() : Net.HttpResponseListener {
        override fun handleHttpResponse(httpResponse: Net.HttpResponse) {
            val statusCode = httpResponse.status.statusCode
            if (statusCode in 200..299) {
                val responseText = httpResponse.resultAsString
                Gdx.app.log("Dreamlo", "Score added successfully: $responseText")
            } else {
                Gdx.app.error("Dreamlo", "Failed to add score. Status code: $statusCode")
            }
        }

        override fun failed(t: Throwable) {
            Gdx.app.error("Dreamlo", "Request failed", t)
        }

        override fun cancelled() {
            Gdx.app.log("Dreamlo", "Request cancelled")
        }
    }

    fun interface OnResult {
        fun onResult(dreamloLeaderboard: DreamloLeaderboard?)
    }

    private class ResponseListener(private val onResult: OnResult) : Net.HttpResponseListener {
        override fun handleHttpResponse(response: Net.HttpResponse) {
            val statusCode = response.status.statusCode
            if (statusCode in 200..299) {
                val jsonData = response.resultAsString

                val result = parseLeaderboard(jsonData)
                onResult.onResult(result)

                //teavm did not like it for some reason (dependency analysis class not found error)
//                Gdx.app.postRunnable {
//                }
            } else {
                Gdx.app.error("HTTP", "Error: $statusCode")
                onResult.onResult(null)
            }
        }

        override fun failed(t: Throwable) {
            Gdx.app.error("HTTP", "Request failed", t)
            onResult.onResult(null)
        }

        override fun cancelled() {
            Gdx.app.log("HTTP", "Request cancelled")
            onResult.onResult(null)
        }
    }
}

data class DreamloLeaderboard(
    val dreamlo: DreamloContainer? = DreamloContainer()
)

data class DreamloContainer(
    val leaderboard: Leaderboard? = Leaderboard()
)

data class Leaderboard(
    val entry: List<Entry>? = emptyList()
)

data class Entry(
    val name: String = "", val score: Int = -1, val seconds: Int = -1, val text: String = "", val date: String = ""
)
