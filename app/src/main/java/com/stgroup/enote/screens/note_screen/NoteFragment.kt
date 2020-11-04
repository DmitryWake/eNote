package com.stgroup.enote.screens.note_screen

import android.content.res.AssetManager
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.stgroup.enote.R
import com.stgroup.enote.models.NoteModel
import com.stgroup.enote.models.ThemeModel
import com.stgroup.enote.utilities.*
import kotlinx.android.synthetic.main.action_panel_note_rich_text.*
import kotlinx.android.synthetic.main.action_panel_note_theme.*
import kotlinx.android.synthetic.main.fragment_note.*
import java.io.IOException

class NoteFragment(var mNote: NoteModel) : Fragment(R.layout.fragment_note) {

    companion object {
        // Тег для вывода в консоль информации или ошибок
        const val TAG = "NoteTag"

        // Список тем
        var mThemeList: MutableList<ThemeModel> = mutableListOf()

        // Чтобы не было повторной загрузки
        private var isAssetsLoad: Boolean = false

        // Весь экран заметок (временное решение перенести сюда)
        lateinit var mDataContainer: CoordinatorLayout

        // Переменная хранит имя текущей темы, чтоб в случае изменения её пользователем запомнить и сохранить
        lateinit var mCurrentThemeName: String
    }

    // Основное поле ввода текста
    private lateinit var mNoteText: EditText

    // Поле, отвечающие за время изменения заметки
    private lateinit var mDateText: TextView

    // Панель с кнопками для редактирования заметок
    private lateinit var mButtonMenu: LinearLayout

    // Кнопки на панели кнопок
    private lateinit var mInsertImageButton: ImageButton
    private lateinit var mTextStyleButton: ImageButton
    private lateinit var mBackgroundThemeButton: ImageButton

    // Выдвижные панельки для кнопок
    private lateinit var mBottomSheetBehaviorTheme: BottomSheetBehavior<*>
    private lateinit var mBottomSheetBehaviorRichText: BottomSheetBehavior<*>

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ThemeChoiceAdapter

    // Проверка на инициализацию RecyclerView
    private var isRecyclerViewConsists: Boolean = false

    // Получаем ассеты
    private val mAssets: AssetManager = APP_ACTIVITY.assets

    override fun onStart() {
        super.onStart()
        initFields()
        initFunctions()

        // Если загружено, то не загружаем
        if (!isAssetsLoad)
            loadAssets()
        // Если загрузились ассеты, то проводим инициализацию
        if (isAssetsLoad && !isRecyclerViewConsists)
            initRecyclerView()

        loadNote()
    }

    override fun onPause() {
        super.onPause()
        saveNote()
    }

    private fun initFunctions() {
        // Прячем клавиатуру, когда нажимаем не в поле ввода
        mDataContainer.setOnClickListener {
            hideKeyboard()
            hideButtonPanel()
        }

        // Показываем панельку, нажимая на поле для ввода
        mNoteText.setOnClickListener {
            showButtonPanel()
        }

        // Показываем выбор тем, при нажатии на кнопку
        mBackgroundThemeButton.setOnClickListener {
            mBottomSheetBehaviorTheme.state = BottomSheetBehavior.STATE_EXPANDED
        }

        // Показываем возможность редактирования текста
        mTextStyleButton.setOnClickListener {
            mBottomSheetBehaviorRichText.state = BottomSheetBehavior.STATE_EXPANDED
            // Изменение текста на жирный
            button_text_bold.setOnClickListener {
                changeTextStyle(StyleSpan(Typeface.BOLD))
            }
            // Изменение текста на курсив
            button_text_italic.setOnClickListener {
                changeTextStyle(StyleSpan(Typeface.ITALIC))
            }
            // Изменене текста на подчеркнутый
            button_text_underlined.setOnClickListener {
                toUnderlineText()
            }
        }

    }

    // Для подчеркнутого текста отдельный метод, так как не получается обобщить с другими
    private fun toUnderlineText() {
        val startSelection = mNoteText.selectionStart
        val endSelection = mNoteText.selectionEnd
        if (startSelection < endSelection) {
            val spanString = SpannableString(mNoteText.text)
            spanString.setSpan(
                UnderlineSpan(),
                startSelection,
                endSelection,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            mNoteText.setText(spanString, TextView.BufferType.SPANNABLE)
            mNoteText.setSelection(endSelection)
        }
    }

    // Изменяем стиль текста
    private fun changeTextStyle(style: StyleSpan) {
        val startSelection = mNoteText.selectionStart
        val endSelection = mNoteText.selectionEnd
        // Значит текст выделен
        if (startSelection < endSelection) {
            // Получаем стилизованную строку
            val spanString = SpannableString(mNoteText.text)
            spanString.setSpan(
                style,
                startSelection,
                endSelection,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            // Заменяем наш текст на текст со стилем
            mNoteText.setText(spanString, TextView.BufferType.SPANNABLE)
            // Перемещаем курсор в конец выделения
            mNoteText.setSelection(endSelection)
        }
    }


    private fun initRecyclerView() {
        mRecyclerView = themes_recycler_view

        mAdapter = ThemeChoiceAdapter(mThemeList)

        mRecyclerView.layoutManager =
            LinearLayoutManager(APP_ACTIVITY, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView.adapter = mAdapter
        isRecyclerViewConsists = true
    }

    private fun loadAssets() {
        try {
            val themeNames = mAssets.list(THEMES_FOLDER)
            if (themeNames != null) {
                Log.i(TAG, "Found ${themeNames.size} themes")
                for (name in themeNames) {
                    // Получаем Входящий поток из ассета
                    val inputStream = mAssets.open("$THEMES_FOLDER/$name")
                    // Преобразуем поток в Drawable
                    val img = Drawable.createFromStream(inputStream, null)
                    // Обновление листа тем
                    mThemeList.add(ThemeModel(name.substringBefore('.'), img))
                }
                // Загрузка удалась
                isAssetsLoad = true
            }
        } catch (ioe: IOException) {
            // Загрузка не удалась
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

    private fun initFields() {
        mNoteText = note_edit_text

        mDateText = note_time_text

        mDataContainer = note_data_container

        mButtonMenu = note_button_menu
        mButtonMenu.visibility = View.GONE

        mBackgroundThemeButton = edit_background_button
        mTextStyleButton = edit_text_style_button

        mBottomSheetBehaviorTheme = BottomSheetBehavior.from(bottom_sheet_theme)
        mBottomSheetBehaviorTheme.state = BottomSheetBehavior.STATE_HIDDEN

        mBottomSheetBehaviorRichText = BottomSheetBehavior.from(bottom_sheet_rich_text)
        mBottomSheetBehaviorRichText.state = BottomSheetBehavior.STATE_HIDDEN

        // Обьяляем тему пустой. Потом при загрузке замеки делаем проверку
        mCurrentThemeName = ""
    }


    private fun loadNote() {
        // Устанавливаем текст в поле для ввода
        mNoteText.setText(Html.fromHtml(mNote.text), TextView.BufferType.EDITABLE)
        // Проверяем на наличие корректной темы (бежим по списку тем и ищем совпадение в названии)

        mThemeList.forEach {
            if (it.mThemeName == mNote.background) {
                mDataContainer.background = it.mThemeImage
                mCurrentThemeName = mNote.background
            }
        }
        // Заголовок на тулбаре
        APP_ACTIVITY.title = mNote.name
    }

    private fun saveNote() {
        mNote.text = Html.toHtml(mNoteText.text)
        if (mCurrentThemeName.isNotEmpty())
            mNote.background = mCurrentThemeName

        // Сохраняем NoteModel в строку джсон
        val jsonString = Gson().toJson(mNote)
        // Сохраняем json в хранилище заметок
        NOTES_STORAGE.edit().putString("$STORAGE_NOTES_ID:${mNote.id}", jsonString).apply()
    }

}