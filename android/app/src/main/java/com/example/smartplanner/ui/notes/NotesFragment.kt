package com.example.smartplanner.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartplanner.R
import com.example.smartplanner.data.NoteRepository

class NotesFragment : Fragment() {

    private lateinit var noteInput: EditText
    private lateinit var addButton: Button
    private lateinit var emptyText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteInput = view.findViewById(R.id.editNote)
        addButton = view.findViewById(R.id.buttonAddNote)
        emptyText = view.findViewById(R.id.textEmptyNotes)
        recyclerView = view.findViewById(R.id.recyclerNotes)

        adapter = NoteAdapter(NoteRepository.getNotes()) { noteId ->
            NoteRepository.removeNote(noteId)
            reloadNotes()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        addButton.setOnClickListener {
            val wasAdded = NoteRepository.addNote(noteInput.text.toString())
            if (wasAdded) {
                noteInput.text.clear()
                reloadNotes()
            } else {
                Toast.makeText(requireContext(), "Введите текст заметки", Toast.LENGTH_SHORT).show()
            }
        }

        reloadNotes()
    }

    private fun reloadNotes() {
        adapter.submitList(NoteRepository.getNotes())
        emptyText.visibility = if (NoteRepository.hasNotes()) View.GONE else View.VISIBLE
    }
}
