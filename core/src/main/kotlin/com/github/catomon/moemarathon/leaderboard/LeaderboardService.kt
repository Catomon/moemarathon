package com.github.catomon.moemarathon.leaderboard

import com.github.catomon.moemarathon.utils.OnResult

object LeaderboardService {

    var leaderboardProvider: LeaderboardProvider = SimpleBoardsProvider()

    fun requestLeaderboard(onResult: OnResult<Leaderboard?>) {
        leaderboardProvider.requestLeaderboard(onResult)
    }

    fun submitScore(modeName: String, playerName: String, score: Int, rank: String) {
        leaderboardProvider.submitScore(modeName, playerName, score, rank)
    }
}

