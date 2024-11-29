package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.graphics.Color
import com.github.catomon.moemarathon.Const
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
    private var noAimOn = false

    private val menuScreen: MenuScreen = game.screen as MenuScreen

    init {
        createTable().apply {
            add(newLabel(Const.APP_NAME).apply { color = Color(0.89f, 0.455f, 0.667f, 1f) })
            add(newLabel(Const.APP_VER).apply {
                setFontScale(0.35f); color = Color(0.89f, 0.455f, 0.667f, 1f)
            }).bottom().padLeft(6f).padBottom(6f)
            center().top()
        }

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

        createTable().apply {
            val scoreLabel = newLabel("")
            scoreLabel.setFontScale(0.5f)
            fun updateScoreLabel() {
                var scoreValue = 0
                if (holdNotesOn) scoreValue += 10
                if (noAimOn) scoreValue -= 15
                scoreLabel.setText(if (scoreValue == 0) "" else (if (scoreValue > 0) "+" else "") + "$scoreValue% score")
                scoreLabel.color = if (scoreValue > 0) Color.GREEN else Color.RED
            }
            add(scoreLabel)
            row()
            add(VisTextButton("No-Aim Off").apply {
                label.setFontScale(0.75f)
                addChangeListener {
                    noAimOn = !noAimOn
                    if (noAimOn) {
                        it.setText("No-Aim On (Easier)")
                    } else {
                        it.setText("No-Aim Off")
                    }

                    updateScoreLabel()
                }
            })
            row()
            add(VisTextButton("Hold Notes Off").apply {
                label.setFontScale(0.75f)
                addChangeListener {
                    holdNotesOn = !holdNotesOn
                    if (holdNotesOn) {
                        it.setText("Hold Notes On (Harder)")
                    } else {
                        it.setText("Hold Notes Off")
                    }

                    updateScoreLabel()
                }
            })
            center().bottom()
        }
    }

    private fun chooseDiff(diff: PlaySettings) {
        this@DifficultySelectStage.fadeInAndThen(1f) {
            game.screen =
                PlayScreen(
                    GameMap(MapsManager.collectMapFiles().first { it.name() == diff.maps.first() }),
                    diff.copy(noHoldNotes = !holdNotesOn, noAim = noAimOn)
                )
        }

        //menuScreen.changeStage(MapSelectStage(diff))
    }
}
