package com.github.catomon.polly

import com.badlogic.gdx.Game
import com.badlogic.gdx.utils.ScreenUtils

open class GameMain : Game() {
    override fun create() {
        setScreen(PlayScreen())
    }

    override fun render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f)
        super.render()
    }
}
