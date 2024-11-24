package com.github.catomon.polly.mainmenu

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.catomon.polly.playscreen.playstage.BackgroundActor

open class BgStage : Stage(ScreenViewport(OrthographicCamera().apply { setToOrtho(false) })) {

    val background = BackgroundActor()

    init {
        addActor(background)
    }
}
