package com.github.catomon.moemarathon

import com.github.catomon.moemarathon.difficulties.RankUtil
import com.github.catomon.moemarathon.playscreen.PlayScreen

object Achievements {
    fun checkMapPassAchievement(playScreen: PlayScreen, rank: String) {
        val userSave = GamePref.userSave
        val bekkySkinUnlock = "skin:" + Skins.bekky.name
        if (playScreen.gameMap.file.name()
                .startsWith("Ichijo - Roulette Roulette (ztrot)") && !userSave.unlocks.contains(bekkySkinUnlock) && RankUtil.getRankInt(
                rank
            ) >= RankUtil.getRankInt("A")
        ) {
            GamePref.userSave = userSave.apply { unlocks += bekkySkinUnlock; notify += bekkySkinUnlock }
        }

        val komugiSkinUnlock = "skin:" + Skins.komugi.name
        if (!userSave.unlocks.contains(komugiSkinUnlock) && RankUtil.getRankInt(
                rank
            ) >= RankUtil.getRankInt("A")
        ) {
            GamePref.userSave = userSave.apply { unlocks += komugiSkinUnlock; notify += komugiSkinUnlock }
        }

        GamePref.userSave = userSave
        GamePref.save()
    }
}
