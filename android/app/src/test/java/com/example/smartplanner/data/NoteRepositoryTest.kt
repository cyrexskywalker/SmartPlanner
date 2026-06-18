package com.example.smartplanner.data

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NoteRepositoryTest {

    @Before
    fun setUp() {
        NoteRepository.clearForTests()
    }

    @After
    fun tearDown() {
        NoteRepository.clearForTests()
    }

    @Test
    fun addNote_trimsTextAndShowsNewestFirst() {
        assertTrue(NoteRepository.addNote(" first note "))
        assertTrue(NoteRepository.addNote("second note"))

        val notes = NoteRepository.getNotes()

        assertEquals(listOf("second note", "first note"), notes.map { it.text })
        assertTrue(NoteRepository.hasNotes())
    }

    @Test
    fun addNote_rejectsBlankText() {
        assertFalse(NoteRepository.addNote("   "))

        assertTrue(NoteRepository.getNotes().isEmpty())
        assertFalse(NoteRepository.hasNotes())
    }

    @Test
    fun removeNote_deletesOnlySelectedNote() {
        NoteRepository.addNote("first")
        NoteRepository.addNote("second")
        val noteToRemove = NoteRepository.getNotes().first()

        NoteRepository.removeNote(noteToRemove.id)

        assertEquals(listOf("first"), NoteRepository.getNotes().map { it.text })
    }

    @Test
    fun addNote_assignsStableIncrementalIds() {
        NoteRepository.addNote("first")
        NoteRepository.addNote("second")

        val notes = NoteRepository.getNotes()

        assertEquals(2L, notes[0].id)
        assertEquals(1L, notes[1].id)
    }

    @Test
    fun removeNote_ignoresUnknownId() {
        NoteRepository.addNote("first")

        NoteRepository.removeNote(999L)

        assertEquals(listOf("first"), NoteRepository.getNotes().map { it.text })
    }
}
