package com.stgroup.enote.screens.category_fragment

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.stgroup.enote.models.NoteModel
import com.stgroup.enote.screens.note_screen.NoteFragment
import com.stgroup.enote.utilities.replaceFragment
import kotlinx.android.synthetic.main.note_item.view.*

class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val mNoteItemContainer: ConstraintLayout = view.note_item_container
    private val mNoteNameText: TextView = view.note_name_text
    private val mNoteDateText: TextView = view.note_date_text

    fun draw(note: NoteModel) {
        mNoteNameText.text = note.name
        mNoteDateText.text = note.dateOfChange
        mNoteItemContainer.setOnClickListener {
            replaceFragment(NoteFragment(note), true)
        }
    }
}