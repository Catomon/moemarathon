package com.github.catomon.moemarathon

import com.github.catomon.moemarathon.leaderboard.LeaderboardService.OnResult

var platformSpecific: IPlatformSpecific? = null

interface IPlatformSpecific {

    fun desktopOpenMapsFolder() {

    }

    fun fetchLeaderboard(onResult: OnResult) {

    }
}
