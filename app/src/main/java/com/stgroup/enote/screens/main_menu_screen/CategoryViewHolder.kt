package com.stgroup.enote.screens.main_menu_screen

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.stgroup.enote.models.CategoryModel
import com.stgroup.enote.screens.category_fragment.CategoryFragment
import com.stgroup.enote.utilities.replaceFragment
import kotlinx.android.synthetic.main.category_item.view.*

class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val mDataContainer: ConstraintLayout = view.category_container
    private val mCategoryNameText: TextView = view.category_name_text

    fun draw(category: CategoryModel) {
        mCategoryNameText.text = category.name
        mDataContainer.setOnClickListener {
            replaceFragment(CategoryFragment(category), true)
        }
    }

}