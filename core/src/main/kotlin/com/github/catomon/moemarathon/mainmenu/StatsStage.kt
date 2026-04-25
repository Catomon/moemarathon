package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.github.catomon.moemarathon.*
import com.github.catomon.moemarathon.difficulties.*
import com.github.catomon.moemarathon.leaderboard.LeaderboardService
import com.github.catomon.moemarathon.leaderboard.gameModeScoreModifier
import com.github.catomon.moemarathon.map.GameMap
import com.github.catomon.moemarathon.map.MapsManager
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.ui.actions.UpdateAction
import com.github.catomon.moemarathon.utils.addCover
import com.github.catomon.moemarathon.utils.createTable
import com.github.catomon.moemarathon.utils.fadeInAndThen
import com.github.catomon.moemarathon.utils.removeCover
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newLabel
import com.github.catomon.moemarathon.widgets.newTextButton
import com.kotcrab.vis.ui.widget.*
import kotlin.math.max

class StatsStage(val playScreen: PlayScreen, val isLost: Boolean = false) : BgStage() {

    val stats = playScreen.stats
    val playSets = playScreen.playSets
    var mapResult: Int = -1
        private set
    var playSetsResult: Int = -1
        private set

    val isMarathon get() = playSets != DefaultMapSets.DefaultPlaySets && playSets != DefaultMapSets.UnlockedOnlyPlaySets

    init {
        val totalNotes = stats.misses + stats.greats + stats.oks

        if (playScreen.noHoldNotes) {
            stats.score = (stats.score * (1f - Config.NO_HOLD_NOTES_PENALTY / 100)).toInt()
        }

        stats.score += stats.combo * 100

        val accuracy = if (totalNotes > 0f) {
            val baseAcc = (stats.greats * 1.0f + stats.oks * 0.5f) / totalNotes
            val missPenalty = stats.misses.toFloat() * 0.85f / totalNotes
            maxOf(0f, baseAcc - missPenalty)
        } else 0f
        val rank = when {
            isLost -> "F"
            stats.misses == 0 && stats.oks == 0 -> "SS"
            stats.misses == 0 && accuracy >= 0.90f -> "S"
            accuracy >= 0.80f -> "A"
            accuracy >= 0.70f -> "B"
            accuracy >= 0.60f -> "C"
            accuracy >= 0.50f -> "D"
            else -> "F"
        }

        mapResult = RankUtil.getRankInt(rank)

        if (!isLost)
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
                if (playScreen.noHoldNotes) {
                    row()
                    add(newLabel("Hold Notes Off: ${Config.NO_HOLD_NOTES_PENALTY}% score").also { it.setFontScale(0.75f) })
                }
            }).width(480f)
        }

        checkAchieveMapComplete()

        //navigation buttons
        if (playSets.name == DEFAULT || playSets.name == DefaultMapSets.UnlockedOnlyPlaySets.name) {
            //when maps browse
            createTable(newTextButton("<Maps").addChangeListener {
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

            val mapName = playScreen.gameMap.file.name()
            playSets.ranks[mapName] = rank
            playSets.mapScores[mapName] = stats.score

            val minRank = Config.MIN_RANK
            if (RankUtil.getRankInt(rank) < RankUtil.getRankInt(minRank) || isLost) {
                createTable(newTextButton("Restart>").addChangeListener {
                    this@StatsStage.fadeInAndThen(1f) {
                        game.screen = PlayScreen(playScreen.gameMap, playSets)
                    }
                }).apply {
                    bottom().right()
                }

                if (isLost) {
                    createTable().apply {
                        add(newLabel("You lost :( try again?").also { it.setFontScale(0.75f) })
                        center().bottom().padBottom(16f)
                    }
                } else {
                    createTable().apply {
                        add(newLabel("Get ").also { it.setFontScale(0.75f) })
                        add(newLabel(minRank).also { it.color = Color.BLUE; it.setFontScale(0.75f) })
                        add(newLabel(" or higher to pass, try again!").also { it.setFontScale(0.75f) })
                        center().bottom().padBottom(16f)
                    }
                }
            } else {
                var timeBeforeContinue = 0f
                createTable(newContinueButton().also { button ->
                    this@StatsStage.addListener(object : InputListener() {
                        override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
                            if (keycode == Input.Keys.ESCAPE) {
                                button.clearActions()
                                if (button is VisTextButton)
                                    button.setText("Continue")
                                else
                                    button.findActor<VisLabel>("label").setText("Continue")
                            }
                            return super.keyDown(event, keycode)
                        }
                    })
                    button.addAction(UpdateAction({
                        timeBeforeContinue += it
                        if (button is VisTextButton)
                            button.setText("(" + max(0f, 6 - timeBeforeContinue).toInt() + ")" + " Continue")
                        else
                            button.findActor<VisLabel>("label")
                                .setText("(" + max(0f, 6 - timeBeforeContinue).toInt() + ")" + " Continue")
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
        val newAchievements = mutableListOf<String>()
        val alreadyUnlocked = GamePref.userSave.achievements
        Achievements.list.forEach {
            if (it.type == Achievement.Type.MapComplete) {
                if (!alreadyUnlocked.contains(it.id))
                    if (it.condition(AchieveParam(statsStage = this)))
                        newAchievements.add(it.id)
            }
        }
        val userSave: UserSave = GamePref.userSave
        userSave.achievements.addAll(newAchievements)
        GamePref.userSave = userSave
        GamePref.save()
    }

    private fun checkAchievePlaySetsComplete(resultRankInt: Int) {
        val userSave: UserSave = GamePref.userSave
        Achievements.list.forEach {
            if (it.type == Achievement.Type.PlaySetsComplete) {
                if (!userSave.achievements.contains(it.id))
                    if (it.condition(AchieveParam(statsStage = this, mapSetResult = resultRankInt)))
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

        playSets.maps
        if (nextMap == null) {
            var goodJob = false
            val marathonResultWindow = VisWindow("").also { window ->
                var avg = playSets.ranks.values.map { RankUtil.getRankInt(it) }.map { it.toFloat() }
                    .toFloatArray().average().toFloat()
                if (avg < RankUtil.getRankInt("S") + 0.5f)
                    avg += 0.5f
                val resultRankInt = avg.toInt()
                val resultRank = RankUtil.getRankChar(avg.toInt())
                playSetsResult = resultRankInt
                val rankChar = RankUtil.getRankChar(playSetsResult)

                window.setCenterOnAdd(true)
                when (rankChar) {
                    "SS" -> {
                        window.add("Godlike!")
                        window.row()
                        window.add("Flawless ${playSets.name}!")
                    }

                    "S" -> {
                        window.add("Perfect score!")
                        window.row()
                        window.add("Mastered ${playSets.name}!")
                    }

                    "A" -> {
                        window.add("Amazing!")
                        window.row()
                        window.add("Conquered ${playSets.name}!")
                    }

                    "B", "C" -> {
                        window.add("Nice job!")
                        window.row()
                        window.add("Cleared ${playSets.name}!")
                    }

                    "D", "E" -> {
                        window.add("Not bad.")
                        window.row()
                        window.add("Survived ${playSets.name}!")
                    }

                    else -> {  // F
                        window.add("Did you even try?")
                        window.row()
                        window.add("Somehow finished ${playSets.name}...")
                    }
                }
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
                    table.add(newLabel(resultRank).also {
                        it.color = RankUtil.getRankColor(resultRank); it.setFontScale(1.50f)
                    })
                    if (rankChar != "F")
                        submitScore(
                            playSets.name,
                            GamePref.userSave.name,
                            (playSets.mapScores.values.sum() * gameModeScoreModifier(playSets.name)).toInt(),
                            rankChar
                        )
                    checkAchievePlaySetsComplete(resultRankInt)
                    saveMarathonResult(resultRankInt)

                    if (resultRankInt >= RankUtil.getRankInt("S"))
                        goodJob = true
                })
                window.row()
                window.add(newTextButton(if (rankChar != "F") "OK!" else "ok").addChangeListener {
                    window.stage.removeCover()
                    window.remove()
                })
                window.pack()
            }
            game.menuScreen.changeStage(MenuStage().also { menuStage ->
                menuStage.addCover()
                menuStage.addActor(marathonResultWindow)

                if (goodJob)
                    AudioManager.playSound(AudioManager.konata_good_job)
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

    private fun submitScore(modeName: String, playerName: String, score: Int, rank: String) {
        when (modeName) {
            NORMAL, HARD, INSANE, NON_STOP -> {}
            else -> return
        }

        LeaderboardService.submitScore(modeName.lowercase().replace("-", "_"), playerName, score, rank)
    }

    private fun saveMarathonResult(resultRankInt: Int) {
        GamePref.userSave.also { userSave ->
            if (!userSave.unlocks.contains(DefaultMapSets.NonStop.name)) {
                if ((playSets.name == DefaultMapSets.NormalMarathon.name && resultRankInt >= RankUtil.getRankInt(
                        "S"
                    )) || (playSets.name == DefaultMapSets.HardMarathon.name && resultRankInt >= RankUtil.getRankInt(
                        "B"
                    )) || (playSets.name == DefaultMapSets.InsaneMarathon.name && resultRankInt >= RankUtil.getRankInt(
                        "C"
                    ))
                ) {
                    userSave.unlocks.add(DefaultMapSets.NonStop.name)
                    userSave.notify.add(DefaultMapSets.NonStop.name)
                }
            }

            when (playSets.name) {
                NORMAL -> if (userSave.normalRank < resultRankInt) {
                    userSave.normalRank = resultRankInt
                    GamePref.userSave = userSave; GamePref.save()
                }

                HARD -> if (userSave.hardRank < resultRankInt) {
                    userSave.hardRank = resultRankInt
                    GamePref.userSave = userSave; GamePref.save()
                }

                INSANE -> if (userSave.insaneRank < resultRankInt) {
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
