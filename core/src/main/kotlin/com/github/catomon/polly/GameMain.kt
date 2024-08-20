package com.github.catomon.polly

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.ScreenUtils
import com.github.catomon.polly.utils.setMouseCursor
import com.kotcrab.vis.ui.VisUI

open class GameMain : Game() {
    override fun create() {
        VisUI.load()

        if (Const.IS_DESKTOP)
            setMouseCursor()

        setScreen(PlayScreen())
    }

    override fun render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f)
        super.render()

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            setScreen(PlayScreen())

        if (Gdx.input.isKeyJustPressed(Input.Keys.F11))
            if (Gdx.graphics.isFullscreen)
                Gdx.graphics.setWindowedMode(Gdx.graphics.width, Gdx.graphics.height)
            else
                Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
    }
}
