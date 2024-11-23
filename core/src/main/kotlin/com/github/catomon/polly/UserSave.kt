package com.github.catomon.polly

data class UserSave(
    var name: String = "Player",
    var level: Int = 1,
    var xp: Int = 0,
    var mapRanks: MutableMap<String, Int> = mutableMapOf(),
)
