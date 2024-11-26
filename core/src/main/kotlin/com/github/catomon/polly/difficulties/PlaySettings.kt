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
    val ranks: MutableMap<String, String> = mutableMapOf(),
)

class DefaultPlaySets : PlaySettings(
    DEFAULT,
    emptyList(),
    noteSpawnTime = 1f,
)

class UnlockedOnlyPlaySets : PlaySettings(
    DEFAULT,
    emptyList(),
    noteSpawnTime = 1f,
)

class EasyDiff() : PlaySettings(
    EASY,
    listOf(
        "Katakiri Rekka - (^3^)chu Dere Rhapsody (-Chata-) [vs Easy].osu",
        "iyuna - Emukko Kyun Kyun (cRyo[iceeicee]) [Easy].osu",
        "nao - Kirihirake! GracieStar (Tari) [Easy].osu",
        "KOTOKO - Sakuranbo Kiss ~Bakuhatsu Damo~n~ (banvi) [Easy].osu",
        "solfa feat. Chata - Colorful precious life (Natsu) [Xinely's Easy].osu"
    ),
    3f
)

class NormalDiff() : PlaySettings(
    NORMAL,
    listOf(
        "Katakiri Rekka - (^3^)chu Dere Rhapsody (-Chata-) [vs Normal].osu",
        "iyuna - Emukko Kyun Kyun (cRyo[iceeicee]) [Normal].osu",
        "nao - Kirihirake! GracieStar (Tari) [Normal].osu",
        "KOTOKO - Sakuranbo Kiss ~Bakuhatsu Damo~n~ (banvi) [Normal].osu",
        "solfa feat. Chata - Colorful precious life (Natsu) [lfj's Normal].osu",
        "U - the first the last (Phyrearms) [Normal].osu",
        "IOSYS - Princess Party ~Seishun Kinshi Rei~ (-Chata-) [Normal].osu",
    ),
    2f
)

class HardDiff() : PlaySettings(
    HARD,
    listOf(
        "Katakiri Rekka - (^3^)chu Dere Rhapsody (-Chata-) [vs Laurier's Hard].osu",
        "iyuna - Emukko Kyun Kyun (cRyo[iceeicee]) [Hard].osu",
        "nao - Kirihirake! GracieStar (Tari) [Hard].osu",
        "KOTOKO - Sakuranbo Kiss ~Bakuhatsu Damo~n~ (banvi) [Hard].osu",
        "solfa feat. Chata - Colorful precious life (Natsu) [Hard].osu",
        "U - the first the last (Phyrearms) [Hard].osu",
        "IOSYS - Princess Party ~Seishun Kinshi Rei~ (-Chata-) [Hard].osu",
    ),
    1f
)
