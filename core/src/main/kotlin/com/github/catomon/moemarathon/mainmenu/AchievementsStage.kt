package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.github.catomon.moemarathon.*
import com.github.catomon.moemarathon.difficulties.DefaultMapSets
import com.github.catomon.moemarathon.difficulties.RankUtil
import com.github.catomon.moemarathon.utils.createTable
import com.github.catomon.moemarathon.widgets.addChangeListener
import com.github.catomon.moemarathon.widgets.newLabel
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisTable

class AchievementsStage() :
    BgStage() {

    private val menuScreen: MenuScreen = game.screen as MenuScreen
    private val userSave = GamePref.userSave

    init {
        val contentTable =
            VisTable().apply {
//                setFillParent(true)
                width = 1000f
                val completionLabel = newLabel("Completion: 0%")
                defaults().pad(8f).left()
                add(completionLabel).padTop(80f)
                row()
                add(VisTable().apply {
                    add(VisTable().apply {
                        left()
                        defaults().left()
                        add(VisTable().apply {
                            left()
                            defaults().left()
                            background = VisUI.getSkin().getDrawable("button-over")
                            add("Marathon:").colspan(2)
                            row()
                            add("Normal ").actor.setFontScale(0.75f)
                            add(RankUtil.newRankLabel(userSave.normalRank))
                            row()
                            add("Hard ").actor.setFontScale(0.75f)
                            add(RankUtil.newRankLabel(userSave.hardRank))
                            row()
                            add("Insane ").actor.setFontScale(0.75f)
                            add(RankUtil.newRankLabel(userSave.insaneRank))
                        }).padBottom(16f).fillX().left()
                        row()
                        add(VisTable().apply {
                            background = VisUI.getSkin().getDrawable("button-over")
                            defaults().left()
                            if (userSave.unlocks.contains(DefaultMapSets.NonStop.name)) {
                                add("Non-Stop: ")
                                val nonStopRank = userSave.mapRanks[DefaultMapSets.NonStop.maps.first()]?.id ?: 0
                                add(RankUtil.newRankLabel(nonStopRank)).left()
                            } else {
                                add("Non-Stop: ")
                            }
                        })
                    }).left().fillX()
                    //
                    add().expandX()
                    val ranks = listOf(
                        userSave.normalRank,
                        userSave.hardRank,
                        userSave.insaneRank,
                        userSave.mapRanks[DefaultMapSets.NonStop.maps.first()]?.id ?: 0
                    )
                    var avg = ranks.map { it.toFloat() }
                        .toFloatArray().average().toFloat()
                    if (avg < RankUtil.getRankInt("S") + 0.5f)
                        avg += 0.5f
                    val resultRankInt = if (ranks.any { it == 0 }) 0 else avg.toInt()
                    val resultRank = RankUtil.getRankChar(resultRankInt)
                    val compl = when (avg.toInt()) {
                        7 -> "over9000"
                        6 -> "1000"
                        5 -> if (ranks.any { it < 6 }) "80" else "100"
                        4 -> "70"
                        3 -> "50"
                        2 -> "30"
                        1 -> "10"
                        else -> "0"
                    }
                    completionLabel.setText("Completion: $compl%")
                    //
                    add(VisTable().apply {
                        add("Total\nRank:").right()
                        add(VisImage(SpriteDrawable(assets.mainAtlas.createSprite(if (resultRank != "") resultRank else "q")))).size(
                            320f,
                            320f
                        ).right()
//                        add(resultRank).actor.apply { setFontScale(5f); color = RankUtil.getRankColor(resultRank) }
                    }).fillX().right()
                }).fillX().left()
                row()
                add(VisTable().apply {
                    defaults().left().pad(2f)
                    fun addAchieveLabel(achievement: Achievement) {
                        val isDone = userSave.achievements.contains(achievement.id)
                        add(VisTable().apply {
                            background = VisUI.getSkin().getDrawable("button-over")
                            if (isDone)
                                add(VisImage("checkmark")).size(30f)
                            add(achievement.text).width(920f).actor.also { label ->
                                label.setFontScale(0.50f)
                                label.wrap = true
                                if (isDone)
                                    label.color = Color.GREEN
                            }
                        }).width(950f)

                    }
                    add(VisTable().apply {
                        background = VisUI.getSkin().getDrawable("button-over")
                        add("Goals: ")
                    })
                    Achievements.list.forEach { achievement ->
                        row()
                        addAchieveLabel(achievement)
                    }
                }).padBottom(160f)
            }

        addActor(VisScrollPane(contentTable).also { it.setFillParent(true); scrollFocus = it })

        addGameNameLabel()

        createTable(newBackButton().addChangeListener {
            menuScreen.changeStage(MenuStage(menuScreen))
        }).apply {
            left().bottom()
        }
    }
}
