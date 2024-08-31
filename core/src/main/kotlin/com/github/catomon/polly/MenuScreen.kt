package com.github.catomon.polly

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.catomon.polly.utils.createTable
import com.kotcrab.vis.ui.widget.VisTextButton
import ctmn.petals.widgets.addChangeListener

class MenuScreen(val game: GameMain) : ScreenAdapter() {

    private val menuStage = Stage(ScreenViewport(OrthographicCamera().apply { setToOrtho(false) }))

    init {
        Gdx.input.inputProcessor = menuStage

        menuStage.createTable().apply {
            center()
            add(VisTextButton("Start").addChangeListener {
                game.screen = PlayScreen()
            }).center()
            row()
            add(VisTextButton("Settings")).center()
            row()
            add(VisTextButton("Exit").addChangeListener {
                Gdx.app.exit()
            }).center()
        }
    }

    override fun render(delta: Float) {
        super.render(delta)

        menuStage.act()
        menuStage.draw()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)

        menuStage.viewport.update(width, height, true)
    }

    override fun dispose() {
        super.dispose()

        menuStage.dispose()
    }
}
