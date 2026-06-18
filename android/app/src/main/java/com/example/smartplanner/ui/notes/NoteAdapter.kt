package com.example.smartplanner.ui.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartplanner.R
import com.example.smartplanner.model.Note

class NoteAdapter(
    private var notes: List<Note>,
    private val onRemove: (Long) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view, onRemove)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int = notes.size

    fun submitList(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    class NoteViewHolder(
        view: View,
        private val onRemove: (Long) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val noteText: TextView = view.findViewById(R.id.textNote)
        private val removeButton: ImageButton = view.findViewById(R.id.buttonRemoveNote)

        fun bind(note: Note) {
            noteText.text = note.text
            removeButton.setOnClickListener {
                onRemove(note.id)
            }
        }
    }
}
