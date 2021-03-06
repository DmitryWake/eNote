package com.stgroup.enote.screens.note_screen

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.stgroup.enote.models.ThemeModel
import com.stgroup.enote.utilities.getThemeTextColour
import kotlinx.android.synthetic.main.theme_item.view.*

class ThemeHolder(view: View) : RecyclerView.ViewHolder(view) {

    // Вся поверхность айтема
    private val mContainer: ConstraintLayout = view.theme_container

    // Картинка превью темы
    private val mThemePreview: ImageView = view.theme_item_image

    // Название темы
    private val mThemeText: TextView = view.theme_text

    fun draw(theme: ThemeModel) {
        mThemeText.text = theme.themeName
        mThemePreview.setImageDrawable(theme.themeImage)
        mContainer.setOnClickListener {
            NoteFragment.mDataContainer.background = theme.themeImage
            NoteFragment.mCurrentThemeName = theme.themeName
            NoteFragment.mNoteText.setTextColor(getThemeTextColour(theme.themeName))
        }
    }

}