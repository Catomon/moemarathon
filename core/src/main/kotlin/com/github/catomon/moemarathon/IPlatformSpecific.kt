package com.github.catomon.moemarathon

import com.github.catomon.moemarathon.leaderboard.Leaderboard
import com.github.catomon.moemarathon.utils.OnResult

var platformSpecific: IPlatformSpecific? = null

interface IPlatformSpecific {

    fun desktopOpenMapsFolder() {

    }

    fun fetchLeaderboard(onResult: OnResult<Leaderboard?>) {

    }

    fun submitScore(modeName: String, playerName: String, score: Int, rank: String) {

    }
}
