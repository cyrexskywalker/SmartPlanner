package com.example.smartplanner.data

import com.example.smartplanner.model.Note

object NoteRepository {

    private val notes = mutableListOf<Note>()
    private var nextId = 1L

    fun addNote(text: String): Boolean {
        val trimmedText = text.trim()
        if (trimmedText.isEmpty()) return false

        notes.add(
            Note(
                id = nextId,
                text = trimmedText
            )
        )
        nextId += 1
        return true
    }

    fun removeNote(id: Long) {
        notes.removeAll { it.id == id }
    }

    fun getNotes(): List<Note> {
        return notes.toList().asReversed()
    }

    fun hasNotes(): Boolean {
        return notes.isNotEmpty()
    }

    internal fun clearForTests() {
        notes.clear()
        nextId = 1L
    }
}
