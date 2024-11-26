package com.github.catomon.moemarathon.scene2d.actions

import com.badlogic.gdx.scenes.scene2d.Action

class OneAction(val action: Action.() -> Unit) : Action() {

    var isDone = false

    override fun act(delta: Float): Boolean {
        if (!isDone) {
            action()

            isDone = true
        }

        return true
    }

    override fun restart() {
        isDone = false
    }
}
