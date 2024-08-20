package com.github.catomon.polly

import com.badlogic.gdx.utils.Queue

class NoteMap(
    val chunks: Queue<Chunk> = Queue<Chunk>(10)
) {

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
