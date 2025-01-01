package com.github.catomon.moemarathon.ui.actions

import com.badlogic.gdx.scenes.scene2d.Action

class DelayRepeatAction(private val delay: Float, val action: (delta: Float) -> Boolean) : Action() {

    var isDone = false
        private set
    private var time = delay

    override fun act(delta: Float): Boolean {
        if (!isDone) {
            time += delta

            while (time >= delay) {
                isDone = action.invoke(delta)
                time -= delay
            }
        }

        return isDone
    }
}
