package com.github.catomon.moemarathon.playscreen

interface NoteListener {

    companion object {
        const val MISS = 0
        const val HIT = 1
        //const val EARLY = 2
        //const val LATE = 3
        const val TOO_EARLY = 4
        const val TOO_FAR = 5
        const val NOTE_TRACE_START = 6
        const val HIT_TRACE = 7
    }

    fun onNoteEvent(id: Int, note: Note)
}
