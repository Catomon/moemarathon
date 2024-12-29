package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.github.catomon.moemarathon.*
import com.github.catomon.moemarathon.difficulties.*
import com.github.catomon.moemarathon.map.GameMap
import com.github.catomon.moemarathon.map.MapsManager
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.scene2d.actions.UpdateAction
import com.github.catomon.moemarathon.utils.addCover
import com.github.catomon.moemarathon.utils.createTable
import com.github.catomon.moemarathon.utils.fadeInAndThen
import com.github.catomon.moemarathon.utils.removeCover
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newLabel
import com.kotcrab.vis.ui.widget.*
import kotlin.math.max

class StatsStage(val playScreen: PlayScreen) : BgStage() {

    val stats = playScreen.stats
    val playSets = playScreen.playSets
    var mapResult: Int = -1
        private set
    var playSetsResult: Int = -1
        private set

    val isMarathon get() = playSets != PlaySets.DefaultPlaySets && playSets != PlaySets.UnlockedOnlyPlaySets

    init {
        val totalNotes = MapsManager.createNoteMap(playScreen.gameMap.osuBeatmap).size
        val pGreats = stats.greats.toFloat() / totalNotes.toFloat()
        val pOks = stats.oks.toFloat() / totalNotes.toFloat()
        val pMisses = stats.misses.toFloat() / totalNotes.toFloat()
        if (!playScreen.noHoldNotes) {
            stats.score * 1.1f
        }
        stats.score += stats.combo * 100
        val rank = when {
            pGreats >= 1f && pMisses == 0f -> "SS"
            pGreats >= 0.8f && pMisses == 0f -> "S"
            pGreats >= 0.7f && pMisses <= 0.05f -> "A"
            pGreats >= 0.6f && pMisses <= 0.10f -> "B"
            pGreats >= 0.5f && pMisses <= 0.15f -> "C"
            pGreats >= 0.4f && pMisses <= 0.20f -> "D"
            else -> "F"
        }

        mapResult = RankUtil.getRankInt(rank)

        saveScore(rank)

        createTable().apply {
            add(VisLabel(playScreen.gameMap.file.nameWithoutExtension()).also { it.setFontScale(0.5f) }).colspan(2)
            row()
            add(VisImage(SpriteDrawable(assets.mainAtlas.createSprite(rank)))).size(320f, 320f)
            add(VisTable().apply {
                add(VisLabel("Score: " + stats.score))
                row()
                add(VisLabel("Great: " + stats.greats).also { it.color = Color.ORANGE })
                row()
                add(VisLabel("Good: " + stats.oks).also { it.color = Color.GREEN })
                row()
                add(VisLabel("Miss: " + stats.misses).also { it.color = Color.RED })
                row()
                add(VisLabel("Combo: " + stats.maxCombo))
                if (!playScreen.noHoldNotes) {
                    row()
                    add(newLabel("Hold Notes: +10% score"))
                }
            }).width(480f)
        }

        checkAchieveMapComplete()

        //navigation buttons
        if (playSets.name == DEFAULT || playSets.name == PlaySets.UnlockedOnlyPlaySets.name) {
            //when maps browse
            createTable(VisTextButton("<Maps").addChangeListener {
                game.menuScreen.changeStage(MapSelectStage(playSets))
            }).apply {
                left().bottom()
            }
        } else {
            // when marathon
            createTable(newEndButton().addChangeListener {
                game.menuScreen.changeStage(MenuStage())
            }).apply {
                left().bottom()
            }

            playSets.ranks[playScreen.gameMap.file.name()] = rank

            val minRank = "C"
            if (RankUtil.getRankInt(rank) < RankUtil.getRankInt(minRank)) {
                createTable(VisTextButton("Restart>").addChangeListener {
                    this@StatsStage.fadeInAndThen(1f) {
                        game.screen = PlayScreen(playScreen.gameMap, playSets)
                    }
                }).apply {
                    bottom().right()
                }

                createTable().apply {
                    add(newLabel("Get ").also { it.setFontScale(0.75f) })
                    add(newLabel(minRank).also { it.color = Color.BLUE; it.setFontScale(0.75f) })
                    add(newLabel(" or higher to pass! Try again!").also { it.setFontScale(0.75f) })
                    center().bottom().padBottom(16f)
                }
            } else {
                var timeBeforeContinue = 0f
                createTable(newContinueButton().also { button ->
                    this@StatsStage.addListener(object : InputListener() {
                        override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
                            if (keycode == Input.Keys.ESCAPE) {
                                button.clearActions()
                                if (button is VisTextButton)
                                    button.setText("Continue>")
                            }
                            return super.keyDown(event, keycode)
                        }
                    })
                    button.addAction(UpdateAction({
                        timeBeforeContinue += it
                        if (button is VisTextButton)
                            button.setText("(" + max(0f, 6 - timeBeforeContinue).toInt() + ")" + " Continue>")
                        if (timeBeforeContinue >= 5) {
                            continueMarathon()
                            true
                        } else {
                            false
                        }
                    }))
                    button.addChangeListener {
                        continueMarathon()
                    }
                }).apply {
                    right().bottom()
                }
            }
        }
    }

    private fun checkAchieveMapComplete() {
        val userSave: UserSave = GamePref.userSave
        Achievements.list.forEach {
            if (it.type == Achievement.Type.MapComplete) {
                if (!userSave.achievements.contains(it.id))
                    if (it.condition(AchieveParam(statsStage = this)))
                        userSave.achievements.add(it.id)
            }
        }
        GamePref.userSave = userSave
        GamePref.save()
    }

    private fun checkAchievePlaySetsComplete(resultRankInt: Int) {
        val userSave: UserSave = GamePref.userSave
        Achievements.list.forEach {
            if (it.type == Achievement.Type.PlaySetsComplete) {
                if (!userSave.achievements.contains(it.id))
                    if (it.condition(AchieveParam(statsStage = this, playSetsResult = resultRankInt)))
                        userSave.achievements.add(it.id)
            }
        }
        GamePref.userSave = userSave
        GamePref.save()
    }

    private fun saveScore(rank: String) {
        val rankInt = RankUtil.getRankInt(rank)
        val userSave = GamePref.userSave
        userSave.mapRanks[playScreen.gameMap.file.name()]?.let { existingRank ->
            if (existingRank.score > stats.score)
                return
        }
        userSave.mapRanks[playScreen.gameMap.file.name()] =
            Rank(rankInt, stats.score, stats.greats, stats.oks, stats.misses, stats.combo)
        GamePref.userSave = userSave
        GamePref.save()
    }

    private fun continueMarathon() {
        val nextMapIndex = playSets.maps.indexOf(playScreen.gameMap.file.name())
        val nextMap = playSets.maps.getOrNull(if (nextMapIndex < 0) -1 else nextMapIndex + 1)
        if (nextMap == null) {
            val marathonResultWindow = VisWindow("Congrats!").also { window ->
                window.setCenterOnAdd(true)
                window.add("You completed ${playSets.name} mode!")
                window.row()
                window.add(VisTable().also { table ->
                    playSets.ranks.forEach { itRank ->
                        table.add(newLabel(itRank.value + " ").also {
                            it.color = RankUtil.getRankColor(itRank.value)
                        })
                        if (playSets.ranks.keys.indexOf(itRank.key) + 1 % 10 == 0)
                            table.row()
                    }
                })
                window.row()
                window.add(VisTable().also { table ->
                    table.add("Result: ")
                    var avg = playSets.ranks.values.map { RankUtil.getRankInt(it) }.map { it.toFloat() }
                        .toFloatArray().average().toFloat()
                    if (avg < RankUtil.getRankInt("S") + 0.5f)
                        avg += 0.5f
                    val resultRankInt = avg.toInt()
                    val resultRank = RankUtil.getRankChar(avg.toInt())
                    table.add(newLabel(resultRank).also {
                        it.color = RankUtil.getRankColor(resultRank); it.setFontScale(1.50f)
                    })

                    playSetsResult = resultRankInt
                    checkAchievePlaySetsComplete(resultRankInt)

                    saveMarathonResult(resultRankInt)
                })
                window.row()
                window.add(VisTextButton("OK!").addChangeListener {
                    window.stage.removeCover()
                    window.remove()
                })
                window.pack()
            }
            game.menuScreen.changeStage(MenuStage().also { menuStage ->
                menuStage.addCover()
                menuStage.addActor(marathonResultWindow)
            })
        } else {
            this@StatsStage.fadeInAndThen(1f) {
                game.screen = PlayScreen(
                    GameMap(MapsManager.collectMapFiles().first { it.name() == nextMap }),
                    playSets
                )
            }
        }
    }

    private fun saveMarathonResult(resultRankInt: Int) {
        GamePref.userSave.also { userSave ->
            if (!userSave.unlocks.contains(PlaySets.NonStop.name)) {
                if ((playSets.name == PlaySets.NormalMarathon.name && resultRankInt >= RankUtil.getRankInt(
                        "S"
                    )) || (playSets.name == PlaySets.HardMarathon.name && resultRankInt >= RankUtil.getRankInt(
                        "B"
                    )) || (playSets.name == PlaySets.InsaneMarathon.name && resultRankInt >= RankUtil.getRankInt(
                        "C"
                    ))
                ) {
                    userSave.unlocks.add(PlaySets.NonStop.name)
                    userSave.notify.add(PlaySets.NonStop.name)
                }
            }

            when (playSets.name) {
                EASY -> if (userSave.normalRank < resultRankInt) {
                    userSave.normalRank = resultRankInt
                    GamePref.userSave = userSave; GamePref.save()
                }

                NORMAL -> if (userSave.hardRank < resultRankInt) {
                    userSave.hardRank = resultRankInt
                    GamePref.userSave = userSave; GamePref.save()
                }

                HARD -> if (userSave.insaneRank < resultRankInt) {
                    userSave.insaneRank = resultRankInt
                    GamePref.userSave = userSave; GamePref.save()
                }
            }
        }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        super.touchDown(screenX, screenY, pointer, button)
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        //onReturn()

        return super.touchUp(screenX, screenY, pointer, button)
    }
}
