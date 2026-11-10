package com.github.catomon.moemarathon.mainmenu

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.catomon.moemarathon.GamePref
import com.github.catomon.moemarathon.playscreen.playstage.BackgroundActor

open class BgStage : Stage(ScreenViewport(OrthographicCamera().apply { setToOrtho(false) })) {

    val background = BackgroundActor()

    init {
        (viewport as ScreenViewport).setUnitsPerPixel(1f / GamePref.uiScale)
        addActor(background)
    }
}
