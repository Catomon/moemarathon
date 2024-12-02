package com.github.catomon.moemarathon

import com.github.catomon.moemarathon.difficulties.PlaySets
import com.github.catomon.moemarathon.difficulties.RankUtil
import com.github.catomon.moemarathon.mainmenu.MenuStage
import com.github.catomon.moemarathon.mainmenu.StatsStage

object Achievements {

    val list = listOf(
        Achievement("b_any_map", "Get B rank on any map", Achievement.Type.MapComplete) {
            (it.statsStage?.mapResult ?: 0) >= RankUtil.getRankInt("B")
        },
        Achievement("a_any_map", "Get A rank on any map (unlocks 'komugi' skin)", Achievement.Type.MapComplete) {
            if ((it.statsStage?.mapResult ?: 0) >= RankUtil.getRankInt("A")) {
                unlockSkin(Skins.komugi)
                true
            } else {
                false
            }
        },
        Achievement(
            "a_any_map_bekky",
            "Get A rank on 'Ichijo - Roulette Roulette' map (unlocks 'bekky' skin)",
            Achievement.Type.MapComplete
        ) {
            if (it.statsStage == null) return@Achievement false
            if (it.statsStage.playScreen.gameMap.file.name().startsWith("Ichijo - Roulette Roulette (ztrot)")
                && (it.statsStage.mapResult) >= RankUtil.getRankInt("A")
            ) {
                unlockSkin(Skins.bekky)
                true
            } else {
                false
            }
        },
        Achievement(
            "complete_marathon",
            "Complete any Marathon (unlocks 'Other Maps')",
            Achievement.Type.PlaySetsComplete
        ) {
            if (it.statsStage == null) return@Achievement false
            if (it.statsStage.isMarathon)
                (it.statsStage.mapResult) >= RankUtil.getRankInt("C")
            else false
        },
        Achievement(
            "complete_normal_marathon_s",
            "Complete Normal Marathon with a rank of >= S (unlocks Non-Stop map)",
            Achievement.Type.PlaySetsComplete
        ) {
            if (it.statsStage == null) return@Achievement false
            if (it.statsStage.playSets == PlaySets.NormalMarathon)
                (it.statsStage.mapResult) >= RankUtil.getRankInt("S")
            else false
        },
        Achievement(
            "complete_hard_marathon_b",
            "Complete Hard Marathon with a rank >= B (unlocks Non-Stop map)",
            Achievement.Type.PlaySetsComplete
        ) {
            if (it.statsStage == null) return@Achievement false
            if (it.statsStage.playSets == PlaySets.NormalMarathon)
                (it.statsStage.mapResult) >= RankUtil.getRankInt("B")
            else false
        },
        Achievement(
            "complete_insane_marathon_c",
            "Complete Insane Marathon with a rank >= C (unlocks Non-Stop map)",
            Achievement.Type.PlaySetsComplete
        ) {
            if (it.statsStage == null) return@Achievement false
            if (it.statsStage.playSets == PlaySets.NormalMarathon)
                (it.statsStage.mapResult) >= RankUtil.getRankInt("C")
            else false
        },
        Achievement(
            "complete_insane_nonstop_c",
            "Complete Non-Stop with a rank of >= C",
            Achievement.Type.PlaySetsComplete
        ) {
            if (it.statsStage == null) return@Achievement false
            if (it.statsStage.playSets == PlaySets.NonStop)
                (it.statsStage.mapResult) >= RankUtil.getRankInt("C")
            else false
        },
        Achievement(
            "complete_insane_nonstop_b",
            "Complete Non-Stop with a rank of >= B",
            Achievement.Type.PlaySetsComplete
        ) {
            if (it.statsStage == null) return@Achievement false
            if (it.statsStage.playSets == PlaySets.NonStop)
                (it.statsStage.mapResult) >= RankUtil.getRankInt("B")
            else false
        },
        Achievement(
            "complete_insane_nonstop_a",
            "Complete Non-Stop with a rank of >= A",
            Achievement.Type.PlaySetsComplete
        ) {
            if (it.statsStage == null) return@Achievement false
            if (it.statsStage.playSets == PlaySets.NonStop)
                (it.statsStage.mapResult) >= RankUtil.getRankInt("A")
            else false
        },
        Achievement(
            "complete_insane_nonstop_s",
            "Complete Non-Stop with a rank of >= S",
            Achievement.Type.PlaySetsComplete
        ) {
            if (it.statsStage == null) return@Achievement false
            if (it.statsStage.playSets == PlaySets.NonStop)
                (it.statsStage.mapResult) >= RankUtil.getRankInt("S")
            else false
        },
    )

    fun unlockSkin(skin: Skin) {
        val userSave = GamePref.userSave
        val skinUnlock = "skin:" + skin.name
        if (!userSave.unlocks.contains(skinUnlock)) {
            userSave.apply { unlocks += skinUnlock; notify += skinUnlock }
            GamePref.userSave = userSave
            GamePref.save()
        }
    }
}

class AchieveParam(
    val menuStage: MenuStage? = null,
    val statsStage: StatsStage? = null,
    val playSetsResult: Int? = null
) {

}

class Achievement(
    val id: String,
    val text: String,
    val type: Type,
    val condition: (param: AchieveParam) -> Boolean,
) {
    enum class Type {
        MainMenu,
        MapComplete,
        PlaySetsComplete,
    }
}
