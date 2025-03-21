package com.github.catomon.moemarathon.leaderboard

import com.github.catomon.moemarathon.utils.OnResult

interface LeaderboardProvider {
    fun requestLeaderboard(onResult: OnResult<Leaderboard?>)

    fun submitScore(modeName: String, playerName: String, score: Int, rank: String)
}
