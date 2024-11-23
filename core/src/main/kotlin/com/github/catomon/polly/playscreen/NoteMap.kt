package com.github.catomon.polly.playscreen

import com.badlogic.gdx.utils.Queue

class NoteMap(
    val chunks: Queue<Chunk> = Queue<Chunk>(10)
) {

    val size: Int get() = let {
        var s = 0
        chunks.forEach { s += it.notes.size }
        s
    }

    class Chunk(
        val notes: Queue<Note> = Queue<Note>(64)
    ) {
        override fun toString(): String {
            return "Chunk(${notes.joinToString()})"
        }
    }

    override fun toString(): String {
        return "NoteMap(${chunks.joinToString()})"
    }
}
