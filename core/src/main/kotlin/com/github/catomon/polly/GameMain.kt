package com.github.catomon.polly

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.ScreenUtils
import com.github.catomon.polly.utils.setMouseCursor

lateinit var assets: Assets

open class GameMain : Game() {

    companion object {
        var screenWidth = -1
            private set
        var screenHeight = -1
            private set
    }

    override fun create() {
        assets = Assets()

        if (Const.IS_DESKTOP)
            setMouseCursor()

        assets.loadUI()

        setScreen(LoadingScreen(this))
    }

    override fun render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f)
        super.render()

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            AudioManager.mapMusic?.stop()
            setScreen(MenuScreen(this))
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F11))
            if (Gdx.graphics.isFullscreen)
                Gdx.graphics.setWindowedMode(Gdx.graphics.width, Gdx.graphics.height)
            else
                Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
    }

    override fun resize(width: Int, height: Int) {
        screenWidth = width
        screenHeight = height

        super.resize(width, height)
    }
}
