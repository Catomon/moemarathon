package com.github.catomon.moemarathon.leaderboard

class Leaderboard(
    val entries: List<Entry>
) {
    class Entry(
        val modeName: String = "",
        val playerName: String = "Player",
        val score: Int = 0,
        val rank: String = ""
    )
}
