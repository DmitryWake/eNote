package com.stgroup.enote.screens.main_menu_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stgroup.enote.R
import com.stgroup.enote.models.CategoryModel

class MainAdapter(private var dataList: MutableList<CategoryModel>) :
    RecyclerView.Adapter<CategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.draw(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newData: MutableList<CategoryModel>) {
        dataList = newData
        notifyDataSetChanged()
    }
}