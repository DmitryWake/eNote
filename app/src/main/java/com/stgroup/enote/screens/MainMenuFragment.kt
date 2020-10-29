package com.stgroup.enote.screens

import androidx.fragment.app.Fragment
import com.stgroup.enote.R
import com.stgroup.enote.models.NoteModel
import com.stgroup.enote.screens.note_screen.NoteFragment
import com.stgroup.enote.utilities.APP_ACTIVITY
import com.stgroup.enote.utilities.replaceFragment

class MainMenuFragment : Fragment(R.layout.fragment_main_menu) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.mDrawer.enableDrawer()
        // Временно
        val note = NoteModel(
            "New note",
            "null",
            "Test",
            "null",
            "null",
            "null",
            "NightSky"
        )
        replaceFragment(NoteFragment(note))
    }
}