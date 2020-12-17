package com.stgroup.enote.screens.main_menu_screen.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stgroup.enote.R
import com.stgroup.enote.models.NoteModel
import com.stgroup.enote.screens.category_fragment.NoteViewHolder

class SearchAdapter(private var dataList: List<NoteModel>) :
    RecyclerView.Adapter<NoteViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.draw(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newData: List<NoteModel>) {
        dataList = newData
        notifyDataSetChanged()
    }
}