package com.github.catomon.polly.difficulties

open class Difficulty(
    val name: String,
    val maps: List<String>,
    val noteSpawnTime: Float
)

class EasyDiff() : Difficulty(
    "Easy",
    listOf(
        "Katakiri Rekka - (^3^)chu Dere Rhapsody (-Chata-) [vs Easy].osu"
    ),
    3f
)

class NormalDiff() : Difficulty(
    "Normal",
    listOf(
        "Katakiri Rekka - (^3^)chu Dere Rhapsody (-Chata-) [vs Normal].osu"
    ),
    2f
)

class HardDiff() : Difficulty(
    "Hard",
    listOf(
        "Katakiri Rekka - (^3^)chu Dere Rhapsody (-Chata-) [vs Laurier's Hard].osu"
    ),
    1f
)
