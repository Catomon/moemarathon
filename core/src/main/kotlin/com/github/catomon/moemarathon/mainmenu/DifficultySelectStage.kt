package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.graphics.Color
import com.github.catomon.moemarathon.Config
import com.github.catomon.moemarathon.GamePref
import com.github.catomon.moemarathon.difficulties.*
import com.github.catomon.moemarathon.difficulties.DefaultMapSets.NormalMarathon
import com.github.catomon.moemarathon.difficulties.DefaultMapSets.InsaneMarathon
import com.github.catomon.moemarathon.difficulties.DefaultMapSets.HardMarathon
import com.github.catomon.moemarathon.game
import com.github.catomon.moemarathon.map.GameMap
import com.github.catomon.moemarathon.map.MapsManager
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.utils.createTable
import com.github.catomon.moemarathon.utils.fadeInAndThen
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newLabel
import com.github.catomon.moemarathon.widgets.newTextButton
import com.kotcrab.vis.ui.widget.VisImage

class DifficultySelectStage() :
    BgStage() {

    private val difficulties: List<GameMapSet> = listOf(
        NormalMarathon, HardMarathon, InsaneMarathon
    )

    private var holdNotesOn = false
    private var noAimOn = false //if (Config.IS_MOBILE) true else false
    private var pointerGameplay = false

    private val menuScreen: MenuScreen = game.screen as MenuScreen

    init {
        addGameNameLabel()

        val userSave = GamePref.userSave
        createTable().apply {
//            if (userSave.unlocks.contains(DefaultMapSets.nonStop.name)) {
                add(newLabel("Marathon").apply { setFontScale(0.5f) }).colspan(3).padTop(20f)
                row()
//            }

            val rankLabelWidth = 60f
            difficulties.forEach { diff ->
                val rankLabelText = when (diff.name) {
                    NORMAL -> RankUtil.getRankChar(userSave.normalRank)

                    HARD -> RankUtil.getRankChar(userSave.hardRank)

                    INSANE -> RankUtil.getRankChar(userSave.insaneRank)

                    else -> ""
                }
                add(newLabel(
                    rankLabelText
                ).also {
                    it.color = RankUtil.getRankColor(rankLabelText)
                }).width(rankLabelWidth)
                add(newTextButton(diff.name).also { button ->
                    button.addChangeListener {
                        chooseDiff(diff)
                    }
                })
                add().width(rankLabelWidth)
                row()
            }

            if (userSave.unlocks.contains(DefaultMapSets.NonStop.name)) {
                add(newLabel("Non-Stop").apply { setFontScale(0.5f) }).colspan(3).padTop(30f)
                row()
                val rankChar = userSave.mapRanks[DefaultMapSets.NonStop.maps.first()]?.id?.let { RankUtil.getRankChar(it) } ?: ""
                add(newLabel(
                    rankChar
                ).also {
                    it.color = RankUtil.getRankColor(rankChar)
                }).width(rankLabelWidth)
                add(newTextButton("Insane").addChangeListener {
                    chooseDiff(DefaultMapSets.NonStop)
                })
                add().width(rankLabelWidth)
            } else {
                add(newLabel("Non-Stop").apply { setFontScale(0.5f) }).colspan(3).padTop(20f)
                row()
                add(newLabel("")).width(rankLabelWidth)
                add(newTextButton("Insane").also { it.add(VisImage("locked")).size(48f) })
                add().width(rankLabelWidth)
            }
        }

        createTable(newBackButton().addChangeListener {
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
                if (noAimOn) scoreValue -= if (Config.IS_MOBILE) 0 else 15
                scoreLabel.setText(if (scoreValue == 0) "" else (if (scoreValue > 0) "+" else "") + "$scoreValue% score")
                scoreLabel.color = if (scoreValue > 0) Color.GREEN else Color.RED
            }
            add(scoreLabel)
            row()
            fun inputDeviceButtonText() = if (pointerGameplay) {
                "Keyboard[ ] Mouse[V]"
            } else {
                "Keyboard[V] Mouse[ ]"
            }
            add(newTextButton(inputDeviceButtonText()).apply {
                label.setFontScale(0.75f)
                addChangeListener {
                    pointerGameplay = !pointerGameplay
                    it.setText(inputDeviceButtonText())
                    updateScoreLabel()
                }
            })
//            row()
//            add(newTextButton("No-Aim Off").apply {
//                label.setFontScale(0.75f)
//                addChangeListener {
//                    noAimOn = !noAimOn
//                    if (noAimOn) {
//                        it.setText("No-Aim On (Easier)")
//                    } else {
//                        it.setText("No-Aim Off")
//                    }
//
//                    updateScoreLabel()
//                }
//            })

            // TODO fix hold notes
//            row()
//            add(newTextButton("Hold Notes Off").apply {
//                label.setFontScale(0.75f)
//                addChangeListener {
//                    holdNotesOn = !holdNotesOn
//                    if (holdNotesOn) {
//                        it.setText("Hold Notes On (Harder)")
//                    } else {
//                        it.setText("Hold Notes Off")
//                    }
//
//                    updateScoreLabel()
//                }
//            })
            center().bottom()
        }
    }

    private fun chooseDiff(diff: GameMapSet) {
        this@DifficultySelectStage.fadeInAndThen(1f) {
            PlayScreen.GameplayConfig.playMethod = if (pointerGameplay) PlayScreen.PlayMethod.POINTER else PlayScreen.GameplayConfig.defaultPlayMethod
            game.screen =
                PlayScreen(
                    GameMap(MapsManager.collectMapFiles().first { it.name() == diff.maps.first() }),
                    diff.copy(noHoldNotes = !holdNotesOn, noAim = noAimOn)
                )
        }
    }
}
