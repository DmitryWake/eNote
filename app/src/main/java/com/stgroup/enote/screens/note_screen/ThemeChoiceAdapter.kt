package com.stgroup.enote.screens.note_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stgroup.enote.R
import com.stgroup.enote.models.ThemeModel

class ThemeChoiceAdapter(private val dataList: MutableList<ThemeModel>) :
    RecyclerView.Adapter<ThemeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.theme_item, parent, false)
        return ThemeHolder(view)
    }

    override fun onBindViewHolder(holder: ThemeHolder, position: Int) {
        holder.draw(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size
}