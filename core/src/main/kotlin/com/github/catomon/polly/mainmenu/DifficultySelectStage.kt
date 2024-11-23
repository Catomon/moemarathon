package com.github.catomon.polly.mainmenu

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.catomon.polly.difficulties.Difficulty
import com.github.catomon.polly.difficulties.EasyDiff
import com.github.catomon.polly.difficulties.HardDiff
import com.github.catomon.polly.difficulties.NormalDiff
import com.github.catomon.polly.game
import com.github.catomon.polly.playscreen.PlayScreen
import com.github.catomon.polly.utils.createTable
import com.github.catomon.polly.widgets.addChangeListener
import com.github.catomon.polly.widgets.newTextButton

class DifficultySelectStage() :
    Stage(ScreenViewport(OrthographicCamera().apply { setToOrtho(false) })) {

    private val difficulties: List<Difficulty> = listOf(
        EasyDiff(), NormalDiff(), HardDiff()
    )

    private val menuScreen: MenuScreen = game.screen as MenuScreen

    init {
        createTable().apply {
            difficulties.forEach { diff ->
                add(newTextButton(diff.name).also { button ->
                    button.userObject = button
                    button.addChangeListener {
                        chooseDiff(diff)
                    }
                })
                row()
            }
        }
    }

    private fun chooseDiff(diff: Difficulty) {
        menuScreen.changeStage(MapSelectStage(menuScreen, diff.maps, onMapSelect = {
            val playScreen = PlayScreen(
                it,
                onReturn = {
                    game.screen = MenuScreen(initialStage = { DifficultySelectStage() })
                    (game.screen as MenuScreen).stage?.clear()
                    ((game.screen as MenuScreen).stage as DifficultySelectStage).chooseDiff(diff)
                }).also { playScreen ->
                playScreen.noteSpawnTime = diff.noteSpawnTime
                playScreen.noTracers = false
            }
            menuScreen.game.screen = playScreen
        }))
    }
}
