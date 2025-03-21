package com.github.catomon.moemarathon.leaderboard

fun gameModeOrderNumber(modeName: String?) = when (modeName ?: "") {
    "normal" -> 2
    "hard" -> 3
    "insane" -> 4
    "non_stop" -> 5
    else -> 0
}

fun gameModeScoreModifier(modeName: String?) = when (modeName ?: "") {
    "normal" -> 1.05f
    "hard" -> 1.10f
    "insane" -> 1.15f
    "non_stop" -> 1.20f
    else -> 1f
}
