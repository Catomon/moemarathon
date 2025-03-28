package com.github.catomon.moemarathon.playscreen.ui

import com.github.catomon.moemarathon.playscreen.Note
import com.github.catomon.moemarathon.playscreen.NoteListener
import com.github.catomon.moemarathon.playscreen.Stats
import com.github.catomon.moemarathon.ui.actions.DelayRepeatAction
import com.kotcrab.vis.ui.widget.VisLabel
import kotlin.math.max

class ScoreLabel(private val stats: Stats) : VisLabel("0", "wborder"), NoteListener {

    private var curScore = 0

    init {
        //setFontScale(1.75f)
        val delay = 0.05f
        addAction(DelayRepeatAction(delay) {
            if (curScore != stats.score) {
                if (curScore < stats.score) {
                    curScore += max(((stats.score - curScore) * 0.5f).toInt(), 1)
                } else {
                    curScore -= max(((curScore - stats.score) * 0.5f).toInt(), 1)
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
