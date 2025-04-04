package com.github.catomon.moemarathon.playscreen

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.github.catomon.moemarathon.Const

class PlayInputProcessor(private val playScreen: PlayScreen) : InputAdapter() {

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.ESCAPE -> {
                playScreen.paused = !playScreen.paused
                if (playScreen.paused) {
                    playScreen.playHud.showMenu()
                } else {
                    playScreen.playHud.hideMenu()
                }
            }

            else -> {
                if (!playScreen.paused) {
                    playScreen.processButtonDown(keycode)
                }
            }
        }

        if (!Const.IS_RELEASE) {
            when (keycode) {
                Input.Keys.W -> {
                    playScreen.hitZoneCircleSize += 0.05f
                    playScreen.mapSize = playScreen.mapSize
                }

                Input.Keys.Q -> {
                    playScreen.noteSize += 0.01f
                    playScreen.mapSize = playScreen.mapSize
                }

                Input.Keys.S -> {
                    playScreen.hitZoneCircleSize -= 0.05f
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
            }
        }

        return super.keyDown(keycode)
    }

    override fun keyUp(keycode: Int): Boolean {
        if (!playScreen.paused) {
            if (playScreen.isHoldingNote && playScreen.holdNoteButton == keycode) {
                playScreen.processButtonDown(keycode)
            }
        }

        return super.keyUp(keycode)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!playScreen.paused) {
            playScreen.processButtonDown(button)
        }

        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!playScreen.paused) {
            if (playScreen.isHoldingNote && playScreen.holdNoteButton == button) {
                playScreen.processButtonDown(button)
            }
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
