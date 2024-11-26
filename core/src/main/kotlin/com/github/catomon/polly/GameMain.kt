package com.github.catomon.polly

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.utils.ScreenUtils
import com.github.catomon.polly.mainmenu.MenuScreen
import com.github.catomon.polly.mainmenu.MenuStage
import com.github.catomon.polly.map.GameMap
import com.github.catomon.polly.playscreen.PlayScreen
import com.github.catomon.polly.utils.setMouseCursor

lateinit var assets: Assets

lateinit var game: GameMain

open class GameMain : Game() {

    lateinit var menuScreen: MenuScreen

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

        game = this

        setScreen(LoadingScreen(this))
    }

    override fun setScreen(screen: Screen?) {
        super.setScreen(screen)

        if (screen is MenuScreen) {
            if (::menuScreen.isInitialized && menuScreen != screen) throw IllegalStateException("menuScreen is already initialized")
            else menuScreen = screen
        }

        if (screen is PlayScreen) screen.ready()
    }

    override fun render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f)
        super.render()

        if (!Const.IS_RELEASE && Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            AudioManager.mapMusic?.stop()
            setScreen(menuScreen)
            menuScreen.changeStage(MenuStage())
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
