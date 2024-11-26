package com.github.catomon.moemarathon

import com.badlogic.gdx.ScreenAdapter
import com.github.catomon.moemarathon.mainmenu.MenuScreen

class LoadingScreen(private val game: GameMain) : ScreenAdapter() {

    init {
        assets.beginLoadingAll()
    }

    override fun render(delta: Float) {
        if (assets.update()) {
            assets.onFinishLoading()
            game.setScreen(MenuScreen())
        }
    }
}
