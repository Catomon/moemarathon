package com.github.catomon.polly

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.math.Vector3

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

            else -> playScreen.clickNote()
        }

        return super.keyDown(keycode)
    }

    override fun keyUp(keycode: Int): Boolean {
        if (playScreen.isTracing) {
            playScreen.clickNote()
        }

        return super.keyUp(keycode)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        tmpVec.x = screenX.toFloat()
        tmpVec.y = screenY.toFloat()
        playScreen.camera.unproject(tmpVec)
        playScreen.pointerX = tmpVec.x
        playScreen.pointerY = tmpVec.y
        playScreen.clickNote()

        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        tmpVec.x = screenX.toFloat()
        tmpVec.y = screenY.toFloat()
        playScreen.camera.unproject(tmpVec)
        playScreen.pointerX = tmpVec.x
        playScreen.pointerY = tmpVec.y
        if (playScreen.isTracing) {
            playScreen.clickNote()
        }

        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        tmpVec.x = screenX.toFloat()
        tmpVec.y = screenY.toFloat()
        playScreen.camera.unproject(tmpVec)
        playScreen.pointerX = tmpVec.x
        playScreen.pointerY = tmpVec.y

        return super.touchDragged(screenX, screenY, pointer)
    }

    private val tmpVec = Vector3()
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        tmpVec.x = screenX.toFloat()
        tmpVec.y = screenY.toFloat()
        playScreen.camera.unproject(tmpVec)
        playScreen.pointerX = tmpVec.x
        playScreen.pointerY = tmpVec.y

        return super.mouseMoved(screenX, screenY)
    }
}