package com.github.catomon.moemarathon

import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.utils.ScreenUtils
import com.github.catomon.moemarathon.difficulties.PlaySets
import com.github.catomon.moemarathon.difficulties.Rank
import com.github.catomon.moemarathon.mainmenu.MenuScreen
import com.github.catomon.moemarathon.mainmenu.MenuStage
import com.github.catomon.moemarathon.playscreen.PlayScreen
import com.github.catomon.moemarathon.utils.setMouseCursor

lateinit var assets: Assets

lateinit var game: GameMain

class GameMain(private val onCreate: (() -> Unit)? = null) : Game() {

    lateinit var menuScreen: MenuScreen

    private val cursorParticle by lazy {
        ParticleEffect().apply {
            load(
                Gdx.files.internal("particles/cursor_particle.p"),
                Gdx.files.internal("particles/")
            )
        }
    }

    companion object {
        var screenWidth = -1
            private set
        var screenHeight = -1
            private set
    }

    override fun create() {
//        Gdx.app.logLevel = Application.LOG_DEBUG

        assets = Assets()

        if (Const.IS_DESKTOP)
            setMouseCursor()

        assets.loadUI()

        game = this

        if (Gdx.app.type == Application.ApplicationType.Desktop) {
            if (System.getProperty("os.name").lowercase().contains("win")) {
                if (!GamePref.fullscreen) {
                    Gdx.graphics.setWindowedMode(Const.WINDOW_WIDTH, Const.WINDOW_HEIGHT)
                } else {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
                }
            }
        }

        onCreate?.invoke()

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

        if (if (Gdx.app.type == Application.ApplicationType.WebGL) Gdx.input.isKeyJustPressed(Input.Keys.F10) else Gdx.input.isKeyJustPressed(
                Input.Keys.F11
            )
        ) {
            if (Gdx.graphics.isFullscreen)
                Gdx.graphics.setWindowedMode(Const.WINDOW_WIDTH, Const.WINDOW_HEIGHT)
            else
                Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
        }

        if (!Const.IS_MOBILE) {
            val batch = when (val screen = screen) {
                is MenuScreen -> screen.stage?.batch
                is PlayScreen -> screen.batch
                else -> null
            }
            if (batch != null) {
                cursorParticle.setPosition(Gdx.input.x.toFloat(), Gdx.graphics.height - Gdx.input.y.toFloat())
                cursorParticle.update(Gdx.graphics.deltaTime)
                batch.begin()
                cursorParticle.draw(batch)
                batch.end()
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        screenWidth = width
        screenHeight = height

        super.resize(width, height)
    }
}
