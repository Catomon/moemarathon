package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.github.catomon.moemarathon.Const
import com.github.catomon.moemarathon.difficulties.RankUtil
import com.github.catomon.moemarathon.game
import com.github.catomon.moemarathon.leaderboard.Entry
import com.github.catomon.moemarathon.leaderboard.LeaderboardService.requestLeaderboard
import com.github.catomon.moemarathon.leaderboard.gameModeOrderNumber
import com.github.catomon.moemarathon.utils.createTable
import com.github.catomon.moemarathon.utils.logErr
import com.github.catomon.moemarathon.utils.logInf
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newLabel
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisTable

class LeaderboardStage() : BgStage() {

    private val menuScreen: MenuScreen = game.screen as MenuScreen

    val contentTable = VisTable().apply {
        width = 900f
        setFillParent(true)
        defaults().left()
    }

    init {
        createTable().apply {
            add(newLabel(Const.APP_NAME).apply { color = Color(0.89f, 0.455f, 0.667f, 1f) })
            add(newLabel(Const.APP_VER).apply {
                setFontScale(0.35f); color = Color(0.89f, 0.455f, 0.667f, 1f)
            }).bottom().padLeft(6f).padBottom(6f)
            center().top()
        }

        fun VisTable.addScore(modeName: String, playerName: String, score: Int, rank: String) {
            val fontScale = 0.50f
            row()
            add(VisLabel(modeName)).width(150f).align(Align.center).actor.also { label ->
                label.setFontScale(fontScale)
                label.wrap = true
                label.setAlignment(Align.center)
            }
            add(VisLabel(playerName)).width(300f).align(Align.center).actor.also { label ->
                label.setFontScale(fontScale)
                label.wrap = true
                label.setAlignment(Align.center)
            }
            add(VisLabel(rank)).width(50f).align(Align.center).actor.also { label ->
                label.color = RankUtil.getRankColor(rank)
                label.setFontScale(fontScale)
                label.wrap = true
                label.setAlignment(Align.center)
            }
            add(VisLabel(score.toString())).width(400f).align(Align.center).actor.also { label ->
                label.setFontScale(fontScale)
                label.wrap = true
                label.setAlignment(Align.center)
            }
//            row()
//            add(Separator()).fillX().align(Align.center).colspan(4)
        }

        contentTable.add("Getting scores...").colspan(4).align(Align.center)

        requestLeaderboard() { board ->
            contentTable.clear()
            if (board == null) {
                logErr("Could not retrieve the leaderboard.")
                contentTable.add("Oopsie, please try later.").colspan(4).align(Align.center)
                return@requestLeaderboard
            }

            val scores = board.dreamlo?.leaderboard?.entry
            if (scores.isNullOrEmpty()) {
                logInf("Leaderboard is empty.")
                contentTable.add("Leaderboard is empty.").colspan(4).align(Align.center)
                return@requestLeaderboard
            }

            contentTable.add("Leaderboard:").colspan(4).align(Align.left)

            val scoresGroupedByMode = scores.groupBy {
                it.text
            }.entries.sortedByDescending {
                gameModeOrderNumber(
                    it.value.firstOrNull()?.text
                )
            }
            val scoresSorted = mutableListOf<Entry>()
            scoresGroupedByMode.forEach {
                scoresSorted.addAll(it.value.sortedByDescending { it.score })
            }

            scoresSorted.forEach {
                val modeNameAndRank = it.text.split("_")
                contentTable.addScore(
                    modeNameAndRank.firstOrNull() ?: "",
                    it.name,
                    it.score,
                    modeNameAndRank.getOrNull(1) ?: ""
                )
            }

            logInf("Leaderboard has ${scoresSorted.size} entries.")
        }

        addActor(VisScrollPane(contentTable).apply {
            setFillParent(true)
            scrollFocus = this
        })

        createTable(newBackButton().addChangeListener {
            menuScreen.changeStage(MenuStage(menuScreen))
        }).apply {
            left().bottom()
        }
    }
}
