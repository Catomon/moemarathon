@file:JvmName("TeaVMLauncher")

package com.github.catomon.moemarathon.teavm

import com.github.catomon.moemarathon.GameMain
import com.github.catomon.moemarathon.IPlatformSpecific
import com.github.catomon.moemarathon.leaderboard.LeaderboardService
import com.github.catomon.moemarathon.platformSpecific
import com.github.xpenatan.gdx.backends.teavm.TeaApplication
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration

/** Launches the TeaVM/HTML application. */
fun main() {
    val config = TeaApplicationConfiguration("canvas").apply {
        //// If width and height are each greater than 0, then the app will use a fixed size.
        //width = 640
        //height = 480
        //// If width and height are both 0, then the app will use all available space.
        //width = 0
        //height = 0
        //// If width and height are both -1, then the app will fill the canvas size.
        width = 0
        height = 0
    }

    val onGameCreate = {
        platformSpecific = object : IPlatformSpecific {
            override fun fetchLeaderboard(onResult: LeaderboardService.OnResult) {
                fetch("http://dreamlo.com/lb/67daf22a8f40bbc22497e381/json", object : FetchCallback {
                    override fun complete(result: String) {
                        onResult.onResult(LeaderboardService.parseLeaderboard(result))
                    }

                    override fun error(message: String) {
                        onResult.onResult(null)
                    }
                })
            }
        }
    }

    TeaApplication(GameMain(onGameCreate), config)
}
