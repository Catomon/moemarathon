package com.github.catomon.moemarathon

import com.github.catomon.moemarathon.difficulties.Rank

data class UserSave(
    var name: String = "Player",
    var score: Int = 0,
    var level: Int = 1,
    var xp: Int = 0,
    var progress: Int = 0,
    var mapRanks: MutableMap<String, Rank> = mutableMapOf(),
    var normalRank: Int = 0,
    var hardRank: Int = 0,
    var insaneRank: Int = 0,
    var unlocks: MutableList<String> = mutableListOf(),
    var notify: MutableList<String> = mutableListOf(),
    var achievements: MutableList<String> = mutableListOf(),
    var skin: String = Skins.default.name,
    var unlockedAllMaps: Int = 0
)

fun newUserSave(name: String) : UserSave {
    return UserSave(
        name = name,
        notify = mutableListOf("tutorial"),
    )
}
