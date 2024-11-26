package com.github.catomon.moemarathon.mainmenu

import com.github.catomon.moemarathon.GamePref
import com.github.catomon.moemarathon.difficulties.*
import com.github.catomon.moemarathon.difficulties.PlaySets.EasyDiff
import com.github.catomon.moemarathon.difficulties.PlaySets.HardDiff
import com.github.catomon.moemarathon.difficulties.PlaySets.NormalDiff
import com.github.catomon.moemarathon.game
import com.github.catomon.moemarathon.map.GameMap
import com.github.catomon.moemarathon.map.MapsManager
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.utils.createTable
import com.github.catomon.moemarathon.utils.fadeInAndThen
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newLabel
import com.github.catomon.moemarathon.widgets.newTextButton
import com.kotcrab.vis.ui.widget.VisTextButton

class DifficultySelectStage() :
    BgStage() {

    private val difficulties: List<PlaySettings> = listOf(
        EasyDiff, NormalDiff, HardDiff
    )

    private var holdNotesOn = false

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

        createTable(VisTextButton("Hold Notes Off (Easier)").apply {
            label.setFontScale(0.75f)
            addChangeListener {
                holdNotesOn = !holdNotesOn
                if (holdNotesOn) {
                    it.setText("Hold Notes On (Harder)")
                } else {
                    it.setText("Hold Notes Off (Easier)")
                }
            }
        }).apply {
            center().bottom()
        }
    }

    private fun chooseDiff(diff: PlaySettings) {
        this@DifficultySelectStage.fadeInAndThen(1f) {
            game.screen =
                PlayScreen(GameMap(MapsManager.collectMapFiles().first { it.name() == diff.maps.first() }), diff.copy(noHoldNotes = !holdNotesOn))
        }

        //menuScreen.changeStage(MapSelectStage(diff))
    }
}
