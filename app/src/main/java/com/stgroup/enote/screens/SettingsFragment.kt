package com.stgroup.enote.screens

import androidx.fragment.app.Fragment
import com.stgroup.enote.R
import com.stgroup.enote.utilities.APP_ACTIVITY


class SettingsFragment: Fragment(R.layout.fragment_settings) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.mDrawer.enableDrawer()
    }


}