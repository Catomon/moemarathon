package com.github.catomon.polly.gameplay

import com.badlogic.gdx.math.Vector2

interface NoteListener {
    fun onNoteEvent(id: Int, notePos: Vector2)
}
