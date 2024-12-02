package com.github.catomon.moemarathon.difficulties

const val DEFAULT = "Default"
const val EASY = "Normal"
const val NORMAL = "Hard"
const val HARD = "Insane"

data class PlaySettings(
    val name: String,
    val maps: List<String>,
    val noteSpawnTime: Float,
    val noHoldNotes: Boolean = true,
    val noAim: Boolean = false,
    val ranks: MutableMap<String, String> = mutableMapOf(),
) {
    override fun equals(other: Any?): Boolean {
        return other is PlaySettings && name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

object PlaySets {
    val DefaultPlaySets = PlaySettings(
        DEFAULT,
        emptyList(),
        noteSpawnTime = 1f,
    )

    val UnlockedOnlyPlaySets = PlaySettings(
        "UnlockedOnlyPlaySets",
        emptyList(),
        noteSpawnTime = 1f,
    )

    val NonStop = PlaySettings(
        "Non-Stop",
        listOf("Lucky Star no Minna - Kumikyoku 'Lucky Star Douga' (Andrea) [Marathon].osu"),
        1f
    )

    val NormalMarathon = PlaySettings(
        EASY,
        listOf(
            "Katakiri Rekka - (^3^)chu Dere Rhapsody (-Chata-) [vs Easy].osu",
            "iyuna - Emukko Kyun Kyun (cRyo[iceeicee]) [Easy].osu",
            "nao - Kirihirake! GracieStar (Tari) [Easy].osu",
            "KOTOKO - Sakuranbo Kiss ~Bakuhatsu Damo~n~ (banvi) [Easy].osu",
            "solfa feat. Chata - Colorful precious life (Natsu) [Xinely's Easy].osu",
            "Yousei Teikoku - Torikago (Furawa) [Easy].osu",
            "Yousei Teikoku - Senketsu no Chikai (Saten) [Weez's Easy].osu",
            "Ichijo - Roulette Roulette (ztrot) [Easy].osu",
        ),
        3f
    )

    val HardMarathon = PlaySettings(
        NORMAL,
        listOf(
            "Katakiri Rekka - (^3^)chu Dere Rhapsody (-Chata-) [vs Normal].osu",
            "iyuna - Emukko Kyun Kyun (cRyo[iceeicee]) [Normal].osu",
            "nao - Kirihirake! GracieStar (Tari) [Normal].osu",
            "KOTOKO - Sakuranbo Kiss ~Bakuhatsu Damo~n~ (banvi) [Normal].osu",
            "solfa feat. Chata - Colorful precious life (Natsu) [lfj's Normal].osu",
            "U - the first the last (Phyrearms) [Normal].osu",
            "IOSYS - Princess Party ~Seishun Kinshi Rei~ (-Chata-) [Normal].osu",
            "Yousei Teikoku - Torikago (Furawa) [yoru].osu",
            "Yousei Teikoku - Senketsu no Chikai (Saten) [Nekoo's Normal].osu",
            "Ichijo - Roulette Roulette (ztrot) [Azure's Normal].osu",
        ),
        2f
    )

    val InsaneMarathon = PlaySettings(
        HARD,
        listOf(
            "Katakiri Rekka - (^3^)chu Dere Rhapsody (-Chata-) [vs Laurier's Hard].osu",
            "iyuna - Emukko Kyun Kyun (cRyo[iceeicee]) [Hard].osu",
            "nao - Kirihirake! GracieStar (Tari) [Hard].osu",
            "KOTOKO - Sakuranbo Kiss ~Bakuhatsu Damo~n~ (banvi) [Hard].osu",
            "solfa feat. Chata - Colorful precious life (Natsu) [Hard].osu",
            "U - the first the last (Phyrearms) [Hard].osu",
            "IOSYS - Princess Party ~Seishun Kinshi Rei~ (-Chata-) [Hard].osu",
            "Yousei Teikoku - Torikago (Furawa) [Alazy].osu",
            "Yousei Teikoku - Senketsu no Chikai (Saten) [Mafia].osu",
            "Ichijo - Roulette Roulette (ztrot) [Hard].osu",
        ),
        1f
    )
}
