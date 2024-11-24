package com.github.catomon.polly.difficulties

const val DEFAULT = "Default"
const val EASY = "Easy"
const val NORMAL = "Normal"
const val HARD = "Hard"

open class PlaySettings(
    val name: String,
    val maps: List<String>,
    val noteSpawnTime: Float,
    val noTracers: Boolean = true,
)

class DefaultPlaySets : PlaySettings(
    DEFAULT,
    emptyList(),
    noteSpawnTime = 1f,
    true,
)

class EasyDiff() : PlaySettings(
    EASY,
    listOf(
        "Katakiri Rekka - (^3^)chu Dere Rhapsody (-Chata-) [vs Easy].osu",
        "iyuna - Emukko Kyun Kyun (cRyo[iceeicee]) [Easy].osu"
    ),
    3f
)

class NormalDiff() : PlaySettings(
    NORMAL,
    listOf(
        "Katakiri Rekka - (^3^)chu Dere Rhapsody (-Chata-) [vs Normal].osu",
        "iyuna - Emukko Kyun Kyun (cRyo[iceeicee]) [Normal].osu"
    ),
    2f
)

class HardDiff() : PlaySettings(
    HARD,
    listOf(
        "Katakiri Rekka - (^3^)chu Dere Rhapsody (-Chata-) [vs Laurier's Hard].osu",
        "iyuna - Emukko Kyun Kyun (cRyo[iceeicee]) [Hard].osu"
    ),
    1f
)
