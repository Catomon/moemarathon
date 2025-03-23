@file:JvmName("TeaVMLauncher")

package com.github.catomon.moemarathon.teavm

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Logger
import com.github.catomon.moemarathon.Const
import com.github.catomon.moemarathon.GameMain
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

    Const.IS_WEBAPP = true

    val onGameCreate = {
        if (!Const.IS_RELEASE)
            Gdx.app.logLevel = Logger.INFO

//        platformSpecific = object : IPlatformSpecific {
//            override fun fetchLeaderboard(onResult: OnResult) {
//                fetch("http://dreamlo.com/lb/67daf22a8f40bbc22497e381/json", object : FetchCallback {
//                    override fun complete(result: String) {
//                        logInf("fetchLeaderboard complete: $result")
//                        onResult.onResult(LeaderboardService.parseLeaderboard(result))
//                    }
//
//                    override fun error(message: String) {
//                        logInf("fetchLeaderboard error: $message")
//                        onResult.onResult(null)
//                    }
//                })
//            }
//
//            override fun submitScore(modeName: String, playerName: String, score: Int, rank: String) {
//                com.github.catomon.moemarathon.teavm.leaderboard.submitScore(
//                    "http://dreamlo.com/lb/$key/add/$playerName/$score/-1/${modeName + "_" + rank}",
//                    object : FetchCallback {
//                        override fun complete(result: String) {
//                            logInf("Score submit: $result")
//                        }
//
//                        override fun error(message: String) {
//                            logErr("Score submit: $message")
//                        }
//                    })
//            }
//        }
    }

    TeaApplication(GameMain(onGameCreate), config)
}
