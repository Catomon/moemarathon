package com.github.catomon.polly.mainmenu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.github.catomon.polly.GamePref
import com.github.catomon.polly.assets
import com.github.catomon.polly.difficulties.*
import com.github.catomon.polly.game
import com.github.catomon.polly.map.GameMap
import com.github.catomon.polly.map.MapsManager
import com.github.catomon.polly.playscreen.PlayScreen
import com.github.catomon.polly.utils.addCover
import com.github.catomon.polly.utils.createTable
import com.github.catomon.polly.utils.fadeInAndThen
import com.github.catomon.polly.utils.removeCover
import com.github.catomon.polly.widgets.addChangeListener
import com.github.catomon.polly.widgets.newLabel
import com.kotcrab.vis.ui.widget.*
import kotlin.concurrent.thread

class StatsStage(val playScreen: PlayScreen) : BgStage() {

    private val stats = playScreen.stats
    private val playSets = playScreen.playSets

    init {
        val totalNotes = MapsManager.createNoteMap(playScreen.gameMap.osuBeatmap).size
        val pGreats = stats.greats.toFloat() / totalNotes.toFloat()
        val pOks = stats.oks.toFloat() / totalNotes.toFloat()
        val pMisses = stats.misses.toFloat() / totalNotes.toFloat()
        val rank = when {
            pGreats >= 1f && pMisses == 0f -> "SS"
            pGreats >= 0.8f && pMisses == 0f -> "S"
            pGreats >= 0.7f && pMisses <= 0.05f -> "A"
            pGreats >= 0.6f && pMisses <= 0.10f -> "B"
            pGreats >= 0.5f && pMisses <= 0.15f -> "C"
            pGreats >= 0.4f && pMisses <= 0.20f -> "D"
            else -> "F"
        }
        thread(true) {
            val rankInt = RankUtil.getRankInt(rank)
            val userSave = GamePref.userSave
            userSave.mapRanks[playScreen.gameMap.file.name()]?.let { existingRank ->
                if (existingRank.score > stats.score)
                    return@thread
            }
            userSave.mapRanks[playScreen.gameMap.file.name()] =
                Rank(rankInt, stats.score, stats.greats, stats.oks, stats.misses, stats.combo)
            GamePref.userSave = userSave
            GamePref.save()
        }

        createTable().apply {
            add(VisLabel(playScreen.gameMap.file.nameWithoutExtension()).also { it.setFontScale(0.5f) }).colspan(2)
                .width(768f)
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
            }).width(480f)
        }

        if (playSets.name == DEFAULT) {
            createTable(VisTextButton("<Maps").addChangeListener {
                game.menuScreen.changeStage(MapSelectStage(playSets))
            }).apply {
                left().bottom()
            }
        } else {
            //EASY, NORM, HARD V

            createTable(VisTextButton("<End").addChangeListener {
                game.menuScreen.changeStage(MenuStage())
            }).apply {
                left().bottom()
            }

            playSets.ranks[playScreen.gameMap.file.name()] = rank

            val minRank = "C"
            if (RankUtil.getRankInt(rank) < RankUtil.getRankInt(minRank)) {
                createTable(VisTextButton("Restart>").addChangeListener {
                    this@StatsStage.fadeInAndThen(0.5f) {
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
                createTable(VisTextButton("Continue>").addChangeListener {
                    val nextMapIndex = playSets.maps.indexOf(playScreen.gameMap.file.name())
                    val nextMap = playSets.maps.getOrNull(if (nextMapIndex < 0) -1 else nextMapIndex + 1)
                    if (nextMap == null) {
                        game.menuScreen.changeStage(MenuStage().also { menuStage ->
                            menuStage.addCover()
                            menuStage.addActor(VisWindow("Congrats!").also { window ->
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

                                    //Save mode resul Rank
                                    GamePref.userSave.also { userSave ->
                                        when (playSets.name) {
                                            EASY -> if (userSave.easyRank < resultRankInt) {
                                                userSave.easyRank = resultRankInt
                                                GamePref.userSave = userSave; GamePref.save()
                                            }

                                            NORMAL -> if (userSave.normalRank < resultRankInt) {
                                                userSave.normalRank = resultRankInt
                                                GamePref.userSave = userSave; GamePref.save()
                                            }

                                            HARD -> if (userSave.hardRank < resultRankInt) {
                                                userSave.hardRank = resultRankInt
                                                GamePref.userSave = userSave; GamePref.save()
                                            }
                                        }
                                    }
                                })
                                window.row()
                                window.add(VisTextButton("OK!").addChangeListener {
                                    window.remove()
                                    menuStage.removeCover()
                                })
                                window.pack()
                            })
                        })
                    } else {
                        this@StatsStage.fadeInAndThen(0.5f) {
                            game.screen = PlayScreen(
                                GameMap(MapsManager.collectMapFiles().first { it.name() == nextMap }),
                                playSets
                            )
                        }
                    }
                }).apply {
                    right().bottom()
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
