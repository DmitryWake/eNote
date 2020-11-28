package com.stgroup.enote.objects

import android.text.Editable
import android.text.TextWatcher

class CodeTextWatcher(private val function: () -> Unit) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (before != 1) {
            function()
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }
}