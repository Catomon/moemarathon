package com.github.catomon.moemarathon.playscreen

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.github.catomon.moemarathon.Config
import com.github.catomon.moemarathon.Config.HIDE_CURSOR_AFTER
import com.github.catomon.moemarathon.utils.defaultCursor
import com.github.catomon.moemarathon.utils.setMouseCursor

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
                return true
            }

            else -> {
                if (!playScreen.paused) {
                   return playScreen.processButtonDown(keycode)
                }
            }
        }

        if (!Config.IS_RELEASE) {
            when (keycode) {
                Input.Keys.Q -> {
                    playScreen.hitZoneCircleSize += 0.05f
                    playScreen.mapSize = playScreen.mapSize
                    return true
                }

                Input.Keys.E -> {
                    playScreen.noteSize += 0.01f
                    playScreen.mapSize = playScreen.mapSize
                    return true
                }

                Input.Keys.W -> {
                    playScreen.hitZoneCircleSize -= 0.05f
                    playScreen.mapSize = playScreen.mapSize
                    return true
                }

                Input.Keys.R -> {
                    playScreen.noteSize -= 0.01f
                    playScreen.mapSize = playScreen.mapSize
                    return true
                }

                Input.Keys.F1 -> {
                    playScreen.debug = !playScreen.debug
                    return true
                }

                Input.Keys.F2 -> {
                    playScreen.paused = !playScreen.paused
                    return true
                }

                Input.Keys.F3 -> {
                    playScreen.autoPlay = !playScreen.autoPlay
                    return true
                }

                Input.Keys.T -> {
                    playScreen.stats.greats = 9999
                    playScreen.onDone()
                    return true
                }

                Input.Keys.Y -> {
                    playScreen.onDone()
                    return true
                }
            }
        }

        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        if (!playScreen.paused) {
            if (playScreen.isHoldingNote && playScreen.holdNoteButton == keycode) {
                playScreen.processButtonDown(keycode)
                return true
            }
        }

        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!playScreen.paused) {
            return playScreen.processButtonDown(button)
        }

        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!playScreen.paused) {
            if (playScreen.isHoldingNote && playScreen.holdNoteButton == button) {
                playScreen.processButtonDown(button)
                return true
            }
        }

        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (!Config.IS_MOBILE) {
            playScreen.cursorHideTime = HIDE_CURSOR_AFTER
            setMouseCursor(defaultCursor)
        }
        return super.touchDragged(screenX, screenY, pointer)
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        if (!Config.IS_MOBILE) {
            playScreen.cursorHideTime = HIDE_CURSOR_AFTER
            setMouseCursor(defaultCursor)
        }
        return super.mouseMoved(screenX, screenY)
    }
}
