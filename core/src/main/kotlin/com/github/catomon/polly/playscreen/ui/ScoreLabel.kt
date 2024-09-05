package com.github.catomon.polly.playscreen.ui

import com.github.catomon.polly.playscreen.Note
import com.github.catomon.polly.playscreen.NoteListener
import com.github.catomon.polly.playscreen.Stats
import com.github.catomon.polly.scene2d.actions.DelayRepeatAction
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

    override fun onNoteEvent(id: Int, note: Note) {
        when (id) {
            1, 2, 3 -> {

            }
        }
    }
}
