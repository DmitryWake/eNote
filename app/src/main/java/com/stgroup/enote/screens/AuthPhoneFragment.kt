package com.stgroup.enote.screens


import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.stgroup.enote.R
import com.stgroup.enote.utilities.APP_ACTIVITY
import com.stgroup.enote.utilities.replaceFragment
import com.stgroup.enote.utilities.showToast
import kotlinx.android.synthetic.main.fragment_auth_phone.*


class AuthPhoneFragment : Fragment(R.layout.fragment_auth_phone) {

    private lateinit var mContinueFAB : FloatingActionButton
    private lateinit var mPhoneEditText : EditText
    private var mPhoneNumber : String = ""

    override fun onResume() {
        super.onResume()
        initFields()
        initFunc()
        APP_ACTIVITY.mDrawer.enableDrawer()
    }

    private fun initFields() {

        mContinueFAB = continue_FloatingActionButton
        mPhoneEditText = phone_EditText
    }

    private fun initFunc() {
        mPhoneEditText.addTextChangedListener(object: TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mPhoneNumber = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        mContinueFAB.setOnClickListener {
            if (mPhoneNumber.isEmpty())
                APP_ACTIVITY.showToast("Please, input your phone number")
            else
                replaceFragment(AuthCodeFragment()) }
    }
}