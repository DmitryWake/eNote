package com.stgroup.enote.screens.settings_screen

import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import com.google.firebase.auth.PhoneAuthProvider
import com.stgroup.enote.R
import com.stgroup.enote.database.AUTH
import com.stgroup.enote.database.signIn
import com.stgroup.enote.objects.CodeTextWatcher
import com.stgroup.enote.utilities.APP_ACTIVITY
import com.stgroup.enote.utilities.replaceFragment
import com.stgroup.enote.utilities.showToast
import kotlinx.android.synthetic.main.fragment_auth_code.*

class AuthCodeFragment(private val phoneNumber: String, private val uid: String) :
    Fragment(R.layout.fragment_auth_code) {

    var code = ""

    override fun onStart() {
        super.onStart()
        initFuncs()
    }

    private fun initFuncs() {
        APP_ACTIVITY.title = phoneNumber
        editTextCode1.requestFocus()
        editTextCode1.addTextChangedListener(CodeTextWatcher {
            editTextCode2.requestFocus()
        })
        editTextCode2.addTextChangedListener(CodeTextWatcher {
            editTextCode3.requestFocus()
        })
        editTextCode3.addTextChangedListener(CodeTextWatcher {
            editTextCode4.requestFocus()
        })
        editTextCode4.addTextChangedListener(CodeTextWatcher {
            editTextCode5.requestFocus()
        })
        editTextCode5.addTextChangedListener(CodeTextWatcher {
            editTextCode6.requestFocus()
        })
        editTextCode6.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                code =
                    editTextCode1.text.toString() +
                            editTextCode2.text.toString() +
                            editTextCode3.text.toString() +
                            editTextCode4.text.toString() +
                            editTextCode5.text.toString() +
                            editTextCode6.text.toString()
                enterCode()
            }

        })
    }

    private fun enterCode() {
        val credential = PhoneAuthProvider.getCredential(uid, code)
        AUTH.signInWithCredential(credential).addOnSuccessListener {
            signIn(phoneNumber)
            replaceFragment(SettingsFragment(), true)
        }.addOnFailureListener {
            APP_ACTIVITY.showToast(it.message.toString())
        }
    }
}