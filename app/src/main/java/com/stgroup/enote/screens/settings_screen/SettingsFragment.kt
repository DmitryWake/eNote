package com.stgroup.enote.screens.settings_screen

import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.stgroup.enote.R
import com.stgroup.enote.utilities.APP_ACTIVITY
import com.stgroup.enote.utilities.replaceFragment
import com.stgroup.enote.utilities.showToast
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment: Fragment(R.layout.fragment_settings) {

    private lateinit var mLogInButton : Button
    private lateinit var mLogOutButton : Button
    private lateinit var mPhoneNumber : TextView

    override fun onResume() {
        super.onResume()
        initFields()
        initFunc()
        APP_ACTIVITY.mDrawer.enableDrawer()
    }

    private fun initFields() {

        mLogInButton = login_Button
        mLogOutButton = logout_Button
        mPhoneNumber = phone_TextView
    }

    private fun initFunc() {
        mLogInButton.setOnClickListener { replaceFragment(AuthPhoneFragment())}
        mLogOutButton.setOnClickListener { APP_ACTIVITY.showToast("You logged out") }
    }
}