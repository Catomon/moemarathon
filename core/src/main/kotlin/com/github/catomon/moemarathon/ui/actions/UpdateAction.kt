package com.github.catomon.moemarathon.ui.actions

import com.badlogic.gdx.scenes.scene2d.Action

class UpdateAction(val action: (delta: Float) -> Boolean) : Action() {

    var isDone = false

    override fun act(delta: Float): Boolean {
        if (!isDone) {
            isDone = action.invoke(delta)
        }

        return isDone
    }
}
