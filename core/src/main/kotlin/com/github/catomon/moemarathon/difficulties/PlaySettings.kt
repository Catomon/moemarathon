package com.github.catomon.moemarathon.difficulties

const val DEFAULT = "Default"
const val NORMAL = "Normal"
const val HARD = "Hard"
const val INSANE = "Insane"
const val NON_STOP = "Non-Stop"

data class PlaySettings(
    val name: String,
    val maps: List<String>,
    val noteSpawnTime: Float,
    val noHoldNotes: Boolean = true,
    val noAim: Boolean = false,
    val ranks: MutableMap<String, String> = mutableMapOf(),
    val hitZonesAmount: Int = 6,
    val mapScores: MutableMap<String, Int> = mutableMapOf(),
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
        NON_STOP,
        listOf("Lucky Star no Minna - Kumikyoku 'Lucky Star Douga' (Andrea) [Marathon].osu"),
        1f,
        hitZonesAmount = 12,
    )

    val NormalMarathon = PlaySettings(
        NORMAL,
        listOf(
            "Lucky Star Cast - Hamatte Sabotte Oh My Ga! (Short Ver.) (James) [Easy].osu",
            "Endou Aya - Moe Yousotte nan desu ka (Alice Margatroid) [Easy].osu",
            "Lucky Star Cast - Nande Dattakke Idol (Short ver.) (Vicho-kun) [Easy].osu",
            "Hirano Aya - Dondake Fanfare (Krisom) [Easy].osu",
            "Shimamoto Sumi - Shiawase Negai Kanata Kara (happy30) [Easy Dan].osu",
            "Shimizu Kaori - Mo, Mousou Machine (Krisom) [Easy].osu",
            "Sharlo - Ne Ni Ge de Reset! (Zero__wind) [Normal].osu",
            "Hirano Aya, Katou Emiri, Fukuhara Kaori, Endou Aya - Motteke! Sailor Fuku (TV Size) (Yugu) [Normal].osu",
        ),
        3f
    )

    val HardMarathon = PlaySettings(
        HARD,
        listOf(
            "Lucky Star Cast - Hamatte Sabotte Oh My Ga! (Short Ver.) (James) [Normal].osu",

            "Endou Aya - Moe Yousotte nan desu ka (Alice Margatroid) [Normal].osu",
            "Fukuhara Kaori - Ne Ni Ge de Risetto! (xierbaliti) [Normal].osu",
            "Lucky Star Cast - Nande Dattakke Idol (Short ver.) (Vicho-kun) [Normal].osu",
            "Hirano Aya - Dondake Fanfare (Krisom) [Normal].osu",
            "Shimamoto Sumi - Shiawase Negai Kanata Kara (happy30) [Normal].osu",
            "Shimizu Kaori - Mo, Mousou Machine (Krisom) [Normal].osu",
            "Hirano Aya, Katou Emiri, Fukuhara Kaori, Endou Aya - Motteke! Sailor Fuku (TV Size) (Yugu) [Pata-Mon's Hard].osu",
        ),
        2f
    )

    val InsaneMarathon = PlaySettings(
        INSANE,
        listOf(
            "Lucky Star Cast - Hamatte Sabotte Oh My Ga! (Short Ver.) (James) [Hard].osu",
            "Endou Aya - Moe Yousotte nan desu ka (Alice Margatroid) [Hard].osu",
            "Fukuhara Kaori - Ne Ni Ge de Risetto! (xierbaliti) [Hard].osu",
            "Lucky Star Cast - Nande Dattakke Idol (Short ver.) (Vicho-kun) [taka's Hard].osu",
            "Hirano Aya - Dondake Fanfare (Krisom) [Mafiamaster's Hard].osu",
            "Shimizu Kaori - Mo, Mousou Machine (Krisom) [Andrea's Hard].osu",
            "Hirano Aya, Katou Emiri, Fukuhara Kaori, Endou Aya - Motteke! Sailor Fuku (TV Size) (Yugu) [Insane].osu",
        ),
        1f,
        hitZonesAmount = 12,
    )
}
