package com.stgroup.enote.screens.category_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stgroup.enote.R
import com.stgroup.enote.models.NoteModel

class CategoryAdapter(private var dataList: MutableList<NoteModel>) :
    RecyclerView.Adapter<NoteViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.draw(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newData: MutableList<NoteModel>) {
        dataList = newData
        notifyDataSetChanged()
    }
}