package com.stgroup.enote.screens.note_screen

import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.stgroup.enote.R
import com.stgroup.enote.models.ThemeModel
import com.stgroup.enote.utilities.APP_ACTIVITY
import com.stgroup.enote.utilities.THEMES_FOLDER
import com.stgroup.enote.utilities.hideKeyboard
import kotlinx.android.synthetic.main.action_panel_note_theme.*
import kotlinx.android.synthetic.main.fragment_note.*
import java.io.IOException

class NoteFragment : Fragment(R.layout.fragment_note) {

    // Тег для вывода в консоль информации или ошибок
    companion object {
        const val TAG = "NoteTag"

        var mThemeList: MutableList<ThemeModel> = mutableListOf()
    }

    // Основное поле ввода текста
    private lateinit var mEditText: EditText

    // Поле, отвечающие за время изменения заметки
    private lateinit var mDateText: TextView

    // Весь экран заметок
    private lateinit var mDataContainer: ConstraintLayout

    // Панель с кнопками для редактирования заметок
    private lateinit var mButtonMenu: LinearLayout

    // Кнопки на панели кнопок
    private lateinit var mInsertImageButton: ImageButton
    private lateinit var mTextStyleButton: ImageButton
    private lateinit var mBackgroundThemeButton: ImageButton

    // Выдвижные панельки для кнопок
    private lateinit var mBottomSheetBehaviorTheme: BottomSheetBehavior<*>

    // Получаем ассеты
    private val mAssets: AssetManager = APP_ACTIVITY.assets

    // Чтобы не было повторной загрузки
    private var isAssetsLoad: Boolean = false

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

        // Показываем панельку, нажимая на поле для ввода
        mEditText.setOnClickListener {
            showButtonPanel()
        }

        // Показываем выбор тем, при нажатии на кнопку
        mBackgroundThemeButton.setOnClickListener {
            mBottomSheetBehaviorTheme.state = BottomSheetBehavior.STATE_EXPANDED
            if (!isAssetsLoad)
                loadAssets()
            if (isAssetsLoad)
                initRecyclerView()
        }

    }

    private fun initRecyclerView() {
        val recyclerView = themes_recycler_view

        // Так как мы проверили, выполнилась ли загрузка, то указываем, что mThemeList != null
        val adapter = ThemeChoiceAdapter(mThemeList)

        recyclerView.layoutManager =
            LinearLayoutManager(APP_ACTIVITY, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }

    private fun loadAssets() {
        try {
            val themeNames = mAssets.list(THEMES_FOLDER)
            if (themeNames != null) {
                Log.i(TAG, "Found ${themeNames.size} themes")
                for (name in themeNames) {
                    val inputStream = mAssets.open("$THEMES_FOLDER/$name")
                    val img = Drawable.createFromStream(inputStream, null)
                    mThemeList.add(ThemeModel(name.substringBefore('.'), img))
                }
                isAssetsLoad = true
            }
        } catch (ioe: IOException) {
            Log.e(TAG, "Could not list assets: ${ioe.message.toString()}")
            return
        }
    }

    // Прячем панельку кнопок
    private fun showButtonPanel() {
        mButtonMenu.visibility = View.VISIBLE
    }

    // Показываем панельку кнопок
    private fun hideButtonPanel() {
        mButtonMenu.visibility = View.GONE
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

        mButtonMenu = note_button_menu
        mButtonMenu.visibility = View.GONE

        mBackgroundThemeButton = edit_background_button

        mBottomSheetBehaviorTheme = BottomSheetBehavior.from(bottom_sheet_theme)
        mBottomSheetBehaviorTheme.state = BottomSheetBehavior.STATE_HIDDEN
    }

}