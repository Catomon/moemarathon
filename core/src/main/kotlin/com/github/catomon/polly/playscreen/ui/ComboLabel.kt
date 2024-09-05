package com.github.catomon.polly.playscreen.ui

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.github.catomon.polly.playscreen.Note
import com.github.catomon.polly.playscreen.NoteListener
import com.github.catomon.polly.playscreen.Stats
import com.kotcrab.vis.ui.widget.VisLabel

class ComboLabel(private val stats: Stats) : VisLabel("x0"), NoteListener {

    private val scaleUp = 0.175f

    override fun scaleChanged() {
        super.scaleChanged()

        setFontScale(scaleX, scaleY)
    }

    override fun onNoteEvent(id: Int, note: Note) {
        setText("x" + stats.combo)
        pack()

        when (id) {
            1, 2 ,3 -> {
                addAction(Actions.sequence(
                    Actions.scaleBy(scaleUp, scaleUp, 0.1f, Interpolation.fade),
                    Actions.scaleBy(-scaleUp, -scaleUp, 0.1f, Interpolation.smooth)
                ))
            }
        }
    }
}
