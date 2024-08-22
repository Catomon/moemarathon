package com.github.catomon.polly.ui

import com.badlogic.gdx.math.Vector2
import com.github.catomon.polly.gameplay.NoteListener
import com.github.catomon.polly.gameplay.Stats
import com.github.catomon.polly.uiactions.DelayRepeatAction
import com.kotcrab.vis.ui.widget.VisLabel
import kotlin.math.max

class ScoreLabel(private val stats: Stats) : VisLabel("0"), NoteListener {

    private var curScore = 0

    init {
        val delay = 0.005f
        addAction(DelayRepeatAction(delay) {
            if (curScore != stats.score) {
                if (curScore < stats.score) {
                    curScore += max(((stats.score - curScore) * 0.01f).toInt(), 1)
                } else {
                    curScore -= max(((curScore - stats.score) * 0.01f).toInt(), 1)
                }

                setText(curScore)
                pack()
            }

            false
        })
    }

    override fun onNoteEvent(id: Int, notePos: Vector2) {
        when (id) {
            1, 2, 3 -> {

            }
        }
    }
}
