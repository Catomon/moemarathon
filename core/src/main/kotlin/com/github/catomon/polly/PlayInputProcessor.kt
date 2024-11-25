package com.github.catomon.polly

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.github.catomon.polly.playscreen.PlayScreen

class PlayInputProcessor(private val playScreen: PlayScreen) : InputAdapter() {

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W -> {
                playScreen.circleSize += 0.05f
                playScreen.mapSize = playScreen.mapSize
            }

            Input.Keys.Q -> {
                playScreen.noteSize += 0.01f
                playScreen.mapSize = playScreen.mapSize
            }

            Input.Keys.S -> {
                playScreen.circleSize -= 0.05f
                playScreen.mapSize = playScreen.mapSize
            }

            Input.Keys.E -> {
                playScreen.noteSize -= 0.01f
                playScreen.mapSize = playScreen.mapSize
            }

            Input.Keys.F1 -> {
                playScreen.debug = !playScreen.debug
            }

            Input.Keys.F2 -> {
                playScreen.paused = !playScreen.paused
            }

            Input.Keys.F3 -> {
                playScreen.autoPlay = !playScreen.autoPlay
            }

            Input.Keys.P -> {
                playScreen.stats.greats = 9999
                playScreen.onDone()
            }

            Input.Keys.L -> {
                playScreen.onDone()
            }

            else -> playScreen.clickNote(keycode)
        }

        return super.keyDown(keycode)
    }

    override fun keyUp(keycode: Int): Boolean {
        if (playScreen.isTracing && playScreen.tracingButton == keycode) {
            playScreen.clickNote(keycode)
        }

        return super.keyUp(keycode)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        playScreen.clickNote(button)

        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (playScreen.isTracing && playScreen.tracingButton == button) {
            playScreen.clickNote(button)
        }

        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return super.touchDragged(screenX, screenY, pointer)
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return super.mouseMoved(screenX, screenY)
    }
}
