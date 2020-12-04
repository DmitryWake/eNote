package com.stgroup.enote.screens.settings_screen


import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.stgroup.enote.R
import com.stgroup.enote.database.AUTH
import com.stgroup.enote.database.signIn
import com.stgroup.enote.utilities.APP_ACTIVITY
import com.stgroup.enote.utilities.replaceFragment
import com.stgroup.enote.utilities.showToast
import kotlinx.android.synthetic.main.fragment_auth_phone.*
import java.util.concurrent.TimeUnit


class AuthPhoneFragment : Fragment(R.layout.fragment_auth_phone) {

    private lateinit var mContinueFAB: FloatingActionButton
    private lateinit var mPhoneEditText: EditText
    private var mPhoneNumber: String = ""
    private lateinit var callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onStart() {
        super.onStart()
        initCallback()
        initFields()
        initFunc()
    }

    private fun initFields() {

        mContinueFAB = continue_FloatingActionButton
        mPhoneEditText = phone_EditText
    }

    private fun initFunc() {
        mPhoneEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mPhoneNumber = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        mContinueFAB.setOnClickListener {
            sendCode()
        }

    }

    private fun sendCode() {
        if (mPhoneNumber.isEmpty()) {
            APP_ACTIVITY.showToast("Введите номер")
        } else {
            authUser()
            APP_ACTIVITY.showToast("Проверка...")
        }
    }

    private fun authUser() {
        val options = PhoneAuthOptions.newBuilder(AUTH)
            .setPhoneNumber(mPhoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(APP_ACTIVITY)                 // Activity (for callback binding)
            .setCallbacks(callback)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun initCallback() {
        callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnSuccessListener {
                    signIn(mPhoneNumber)
                    replaceFragment(SettingsFragment(), false)
                }.addOnFailureListener { APP_ACTIVITY.showToast(it.message.toString()) }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                APP_ACTIVITY.showToast(e.message.toString())
            }

            override fun onCodeSent(id: String, p1: PhoneAuthProvider.ForceResendingToken) {
                replaceFragment(AuthCodeFragment(mPhoneNumber, id))
            }

        }
    }
}