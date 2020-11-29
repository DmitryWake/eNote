package com.stgroup.enote.screens.settings_screen

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.stgroup.enote.R
import com.stgroup.enote.database.CURRENT_UID
import com.stgroup.enote.database.USER
import com.stgroup.enote.database.sighOut
import com.stgroup.enote.utilities.APP_ACTIVITY
import com.stgroup.enote.utilities.replaceFragment
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var phoneNumberTextView: TextView
    private lateinit var mLogInButton: Button
    private lateinit var mLogOutButton: Button
    private lateinit var mPhoneNumber: TextView

    override fun onResume() {
        super.onResume()
        initFields()
        initFunc()
        APP_ACTIVITY.mDrawer.enableDrawer()
        APP_ACTIVITY.title = getString(R.string.settings)
    }

    private fun initFields() {
        mLogInButton = login_Button
        mLogOutButton = logout_Button
        mPhoneNumber = phone_TextView
        if (CURRENT_UID.isNotEmpty() && CURRENT_UID != "null") {
            mLogInButton.visibility = View.INVISIBLE
        } else {
            mLogOutButton.visibility = View.INVISIBLE
        }
        phoneNumberTextView = phone_TextView
        if (CURRENT_UID != "null") {
            phoneNumberTextView.text = USER.phoneNumber
        } else {
            phoneNumberTextView.text = getString(R.string.enter_with_phone_text)
            login_text.visibility = View.INVISIBLE
        }
    }

    private fun initFunc() {
        mLogInButton.setOnClickListener { replaceFragment(AuthPhoneFragment()) }
        mLogOutButton.setOnClickListener {
            sighOut()
            phoneNumberTextView.text = getString(R.string.enter_with_phone_text)
            login_text.visibility = View.INVISIBLE
            mLogInButton.visibility = View.VISIBLE
            mLogOutButton.visibility = View.INVISIBLE
        }
    }
}