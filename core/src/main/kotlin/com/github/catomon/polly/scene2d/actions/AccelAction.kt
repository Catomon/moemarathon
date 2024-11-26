package com.github.catomon.moemarathon.scene2d.actions

import com.badlogic.gdx.scenes.scene2d.Action

class AccelAction(val action: (acc: Float) -> Boolean) : Action() {

    var acc = 0f

    var isDone = false

    override fun act(delta: Float): Boolean {
        acc += delta

        if (!isDone) {
            isDone = action.invoke(acc)
        }

        return isDone
    }
}
