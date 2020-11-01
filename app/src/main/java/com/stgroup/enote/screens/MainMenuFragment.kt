package com.stgroup.enote.screens

import androidx.fragment.app.Fragment
import com.stgroup.enote.R
import com.stgroup.enote.utilities.APP_ACTIVITY

class MainMenuFragment : Fragment(R.layout.fragment_main_menu) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.mDrawer.enableDrawer()
        // Временно для открытия заметки
        /*val note = NoteModel(
            "New note",
            "null",
            "Test",
            "null",
            "null",
            "null",
            "NightSky"
        )
        replaceFragment(NoteFragment(note))*/
    }
}