package com.github.catomon.polly

import com.github.catomon.polly.difficulties.Rank

data class UserSave(
    var name: String = "Player",
    var score: Int = 0,
    var level: Int = 1,
    var xp: Int = 0,
    var progress: Int = 0,
    var mapRanks: MutableMap<String, Rank> = mutableMapOf(),
    var easyRank: Int = 0,
    var normalRank: Int = 0,
    var hardRank: Int = 0,
)
