package com.github.catomon.polly.mainmenu

import com.github.catomon.polly.GamePref
import com.github.catomon.polly.difficulties.*
import com.github.catomon.polly.game
import com.github.catomon.polly.map.GameMap
import com.github.catomon.polly.map.MapsManager
import com.github.catomon.polly.playscreen.PlayScreen
import com.github.catomon.polly.utils.createTable
import com.github.catomon.polly.utils.fadeInAndThen
import com.github.catomon.polly.widgets.addChangeListener
import com.github.catomon.polly.widgets.newLabel
import com.github.catomon.polly.widgets.newTextButton
import com.kotcrab.vis.ui.widget.VisTextButton

class DifficultySelectStage() :
    BgStage() {

    private val difficulties: List<PlaySettings> = listOf(
        EasyDiff(), NormalDiff(), HardDiff()
    )

    private val menuScreen: MenuScreen = game.screen as MenuScreen

    init {
        val userSave = GamePref.userSave
        createTable().apply {
            difficulties.forEach { diff ->
                val rankLabelText = when (diff.name) {
                    EASY -> RankUtil.getRankChar(userSave.easyRank)

                    NORMAL -> RankUtil.getRankChar(userSave.normalRank)

                    HARD -> RankUtil.getRankChar(userSave.hardRank)

                    else -> ""
                }
                add(newLabel(
                    rankLabelText
                ).also {
                    it.color = RankUtil.getRankColor(rankLabelText)
                })
                add(newTextButton(diff.name).also { button ->
                    button.userObject = button
                    button.addChangeListener {
                        chooseDiff(diff)
                    }
                })
                row()
            }
        }

        createTable(VisTextButton("<Menu").addChangeListener {
            menuScreen.changeStage(MenuStage(menuScreen))
        }).apply {
            left().bottom()
        }
    }

    private fun chooseDiff(diff: PlaySettings) {
        this@DifficultySelectStage.fadeInAndThen(0.5f) {
            game.screen =
                PlayScreen(GameMap(MapsManager.collectMapFiles().first { it.name() == diff.maps.first() }), diff)
        }

        //menuScreen.changeStage(MapSelectStage(diff))
    }
}
