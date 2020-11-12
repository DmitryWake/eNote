package com.stgroup.enote.screens

import androidx.fragment.app.Fragment
import com.stgroup.enote.R
import com.stgroup.enote.utilities.APP_ACTIVITY

class AuthCodeFragment : Fragment(R.layout.fragment_auth_code) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.mDrawer.enableDrawer()
    }
}