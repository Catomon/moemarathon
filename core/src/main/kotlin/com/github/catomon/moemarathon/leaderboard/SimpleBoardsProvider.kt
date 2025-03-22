package com.github.catomon.moemarathon.leaderboard

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.utils.JsonReader
import com.github.catomon.moemarathon.Const
import com.github.catomon.moemarathon.platformSpecific
import com.github.catomon.moemarathon.utils.OnResult
import com.github.catomon.moemarathon.utils.gson

class SimpleBoardsProvider : LeaderboardProvider {

    private val baseUrl = "https://api.simpleboards.dev/api"

    override fun requestLeaderboard(onResult: OnResult<Leaderboard?>) {
        if (Const.IS_WEBAPP) {
            platformSpecific?.fetchLeaderboard(onResult) ?: run {
                fetchLeaderboard(onResult)
            }
        } else {
            fetchLeaderboard(onResult)
        }
    }

    private fun fetchLeaderboard(onResult: OnResult<Leaderboard?>) {
        val httpRequest = Net.HttpRequest("GET").apply {
            url = "$baseUrl/leaderboards/$boardId/entries"
            setHeader("x-api-key", simpleBoardsApiKey)
        }

        Gdx.net.sendHttpRequest(httpRequest, GetLeaderboardResponseListener(onResult))
    }

    override fun submitScore(modeName: String, playerName: String, score: Int, rank: String) {
        val httpRequest = Net.HttpRequest(Net.HttpMethods.POST).apply {
            url = "$baseUrl/entries"
            setHeader("x-api-key", simpleBoardsApiKey)
            content = """
                {
                  "leaderboardId": "$boardId",
                  "playerId": "",
                  "playerDisplayName": "$playerName",
                  "score": "$score",
                  "metadata": "$modeName,$rank"
                }
            """.trimIndent()
        }

        Gdx.net.sendHttpRequest(
            httpRequest,
            SubmitResponseListener()
        )
    }

    private class GetLeaderboardResponseListener(private val onResult: OnResult<Leaderboard?>) :
        Net.HttpResponseListener {
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

        private fun parseLeaderboard(jsonString: String): Leaderboard? {
            try {
                val leaderboard =
                    JsonReader().parse(jsonString).map { LeaderboardEntry().also { it2 -> gson.readFields(it2, it) } }

                return Leaderboard(leaderboard.map {
                    val modeNameAndRank = it.metadata.split(",")
                    Leaderboard.Entry(
                        modeNameAndRank.firstOrNull() ?: "",
                        it.playerDisplayName,
                        it.score.toInt(),
                        modeNameAndRank.getOrNull(1) ?: ""
                    )
                })
            } catch (e: Exception) {
                Gdx.app.error("SimpleBoards", "Parsing failed", e)
                return null
            }
        }
    }

    private class SubmitResponseListener() : Net.HttpResponseListener {
        override fun handleHttpResponse(httpResponse: Net.HttpResponse) {
            val statusCode = httpResponse.status.statusCode
            if (statusCode in 200..299) {
                val responseText = httpResponse.resultAsString
                Gdx.app.log("SimpleBoards", "Score added successfully: $responseText")
            } else {
                Gdx.app.error("SimpleBoards", "Failed to add score. Status code: $statusCode")
            }
        }

        override fun failed(t: Throwable) {
            Gdx.app.error("SimpleBoards", "Request failed", t)
        }

        override fun cancelled() {
            Gdx.app.log("SimpleBoards", "Request cancelled")
        }
    }

    private class SimpleLeaderboard : ArrayList<LeaderboardEntry>()

    data class LeaderboardEntry(
        val id: String = "",
        val leaderboardId: String = "",
        val playerId: String = "",
        val playerDisplayName: String = "",
        val score: String = "",
        val metadata: String = ""
    )
}
