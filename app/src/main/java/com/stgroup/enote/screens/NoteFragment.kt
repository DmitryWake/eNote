package com.stgroup.enote.screens

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.stgroup.enote.R
import com.stgroup.enote.utilities.hideKeyboard
import kotlinx.android.synthetic.main.fragment_note.*

class NoteFragment : Fragment(R.layout.fragment_note) {

    // Основное поле ввода текста
    private lateinit var mEditText: EditText

    // Поле, отвечающие за время изменения заметки
    private lateinit var mDateText: TextView

    // Весь экран заметок
    private lateinit var mDataContainer: ConstraintLayout
    // Панель с кнопками для редактирования заметок

    override fun onStart() {
        super.onStart()
        initFields()
        initFunctions()
    }

    private fun initFunctions() {
        // Прячем клавиатуру, когда нажимаем не в поле ввода
        mDataContainer.setOnClickListener {
            hideKeyboard()
            hideButtonPanel()
        }

        mEditText.setOnClickListener {
            showButtonPanel()
        }

    }

    private fun showButtonPanel() {
        TODO("Not yet implemented")
    }

    private fun hideButtonPanel() {
        TODO("Not yet implemented")
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun insertImage() {
        val imageSpan = ImageSpan(resources.getDrawable(R.drawable.ic_home))
        val builder = SpannableStringBuilder()
        builder.append(mEditText.text)
        val imgId = "[img=0]"

        val selStart = mEditText.selectionStart
        builder.replace(mEditText.selectionStart, mEditText.selectionEnd, imgId)
        builder.setSpan(
            imageSpan,
            selStart,
            selStart + imgId.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mEditText.text = builder
    }

    private fun initFields() {
        mEditText = note_edit_text
        mDateText = note_time_text
        mDataContainer = note_data_container
    }

}