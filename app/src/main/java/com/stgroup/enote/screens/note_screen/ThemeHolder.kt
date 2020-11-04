package com.stgroup.enote.screens.note_screen

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.stgroup.enote.models.ThemeModel
import kotlinx.android.synthetic.main.theme_item.view.*

class ThemeHolder(view: View) : RecyclerView.ViewHolder(view) {

    // Вся поверхность айтема
    private val mContainer: ConstraintLayout = view.theme_container

    // Картинка превью темы
    private val mThemePreview: ImageView = view.theme_item_image

    // Название темы
    private val mThemeText: TextView = view.theme_text

    fun draw(theme: ThemeModel) {
        mThemeText.text = theme.mThemeName
        mThemePreview.setImageDrawable(theme.mThemeImage)
        mContainer.setOnClickListener {
            NoteFragment.mDataContainer.background = theme.mThemeImage
            NoteFragment.mCurrentThemeName = theme.mThemeName
        }
    }

}