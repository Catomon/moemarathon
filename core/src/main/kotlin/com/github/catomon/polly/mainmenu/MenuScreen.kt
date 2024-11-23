package com.github.catomon.polly.mainmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.catomon.polly.GameMain
import com.github.catomon.polly.scene2d.StageScreen
import com.github.catomon.polly.utils.createTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.github.catomon.polly.widgets.addChangeListener

class MenuScreen(val game: GameMain = com.github.catomon.polly.game, val initialStage: (() -> Stage)? = null) : StageScreen() {
    override fun show() {
        if (initialStage == null) {
            val menuStage = menuStage()

            changeStage(menuStage)
        } else {
            changeStage(initialStage.invoke())
        }
    }

    fun menuStage() : Stage {
        val menuStage = Stage(ScreenViewport(OrthographicCamera().apply { setToOrtho(false) }))
        menuStage.createTable().apply {
            center()
            add(VisTextButton("Start").addChangeListener {
                changeStage(DifficultySelectStage())
            }).center()
            row()
            add(VisTextButton("Maps").addChangeListener {
                changeStage(MapSelectStage(this@MenuScreen))
            }).center()
            row()
            add(VisTextButton("Settings")).center()
            row()
            add(VisTextButton("Exit").addChangeListener {
                Gdx.app.exit()
            }).center()
        }

        return menuStage
    }
}
