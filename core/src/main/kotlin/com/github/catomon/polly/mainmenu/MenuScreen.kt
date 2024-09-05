package com.github.catomon.polly.mainmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.catomon.polly.GameMain
import com.github.catomon.polly.playscreen.PlayScreen
import com.github.catomon.polly.scene2d.StageScreen
import com.github.catomon.polly.utils.createTable
import com.kotcrab.vis.ui.widget.VisTextButton
import ctmn.petals.widgets.addChangeListener

class MenuScreen(val game: GameMain) : StageScreen() {

    private val menuStage = Stage(ScreenViewport(OrthographicCamera().apply { setToOrtho(false) }))

    init {
        changeStage(menuStage)

        menuStage.createTable().apply {
            center()
            add(VisTextButton("Start").addChangeListener {
                game.screen = PlayScreen()
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
    }
}
