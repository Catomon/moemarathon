package com.github.catomon.polly.mainmenu

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.catomon.polly.difficulties.Difficulty
import com.github.catomon.polly.difficulties.EasyDiff
import com.github.catomon.polly.difficulties.HardDiff
import com.github.catomon.polly.difficulties.NormalDiff
import com.github.catomon.polly.playscreen.PlayScreen
import com.github.catomon.polly.utils.createTable
import ctmn.petals.widgets.addChangeListener
import ctmn.petals.widgets.newTextButton

class DifficultySelectStage(val menuScreen: MenuScreen) : Stage(ScreenViewport(OrthographicCamera().apply { setToOrtho(false) })) {

    private val difficulties: List<Difficulty> = listOf(
        EasyDiff(), NormalDiff(), HardDiff()
    )

    init {
        createTable().apply {
            difficulties.forEach { diff ->
                add(newTextButton(diff.name).also { button ->
                    button.userObject = button
                    button.addChangeListener {
                        menuScreen.changeStage(MapSelectStage(menuScreen, diff.maps) {
                            val playScreen = PlayScreen(it).also { playScreen ->
                                playScreen.noteSpawnTime = diff.noteSpawnTime //todo settings
                                playScreen.noTracers = false
                            }
                            menuScreen.game.screen = playScreen
                        })
                    }
                })
                row()
            }
        }
    }
}
