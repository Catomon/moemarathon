package com.github.catomon.moemarathon

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.utils.ScreenUtils
import com.github.catomon.moemarathon.difficulties.PlaySets
import com.github.catomon.moemarathon.difficulties.Rank
import com.github.catomon.moemarathon.mainmenu.MenuScreen
import com.github.catomon.moemarathon.mainmenu.MenuStage
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.utils.setMouseCursor

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
            AudioManager.stopMapMusic()
            setScreen(menuScreen)
            menuScreen.changeStage(MenuStage())
        }

        if (!Const.IS_RELEASE && Gdx.input.isKeyJustPressed(Input.Keys.F6)) {
            GamePref.userSave = UserSave(
                "DEBUG_PLAYER",
                999999999,
                9999999,
                999999,
                999999,
                mutableMapOf(PlaySets.NonStop.maps.first() to Rank(7, 999999, 999999, 99999)),
                7,
                7,
                7,
                unlocks = mutableListOf("Non-Stop")
            )
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F11))
            if (Gdx.graphics.isFullscreen)
                Gdx.graphics.setWindowedMode(Const.WINDOW_WIDTH, Const.WINDOW_HEIGHT)
            else
                Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
    }

    override fun resize(width: Int, height: Int) {
        screenWidth = width
        screenHeight = height

        super.resize(width, height)
    }
}
