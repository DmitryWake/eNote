package com.stgroup.enote.screens.note_screen

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
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
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.stgroup.enote.R
import com.stgroup.enote.models.NoteModel
import com.stgroup.enote.models.ThemeModel
import com.stgroup.enote.utilities.*
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.action_panel_note_rich_text.*
import kotlinx.android.synthetic.main.action_panel_note_theme.*
import kotlinx.android.synthetic.main.fragment_note.*
import java.io.File
import java.io.IOException
import java.util.*

class NoteFragment(var mNote: NoteModel) : Fragment(R.layout.fragment_note) {

    companion object {
        // Тег для вывода в консоль информации или ошибок
        const val TAG = "NoteTag"

        var isNoteLoad = false

        // Список тем
        var mThemeList: MutableList<ThemeModel> = mutableListOf()

        // Чтобы не было повторной загрузки
        private var isAssetsLoad: Boolean = false

        // Весь экран заметок (временное решение перенести сюда)
        lateinit var mDataContainer: CoordinatorLayout

        // Переменная хранит имя текущей темы, чтоб в случае изменения её пользователем запомнить и сохранить
        lateinit var mCurrentThemeName: String

        // Основное поле ввода текста
        lateinit var mNoteText: EditText
    }

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

        if (!isNoteLoad)
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

        mInsertImageButton.setOnClickListener {
            getImage()
        }

    }

    @SuppressLint("SdCardPath")
    private fun getImage() {
        val imageID = UUID.randomUUID().toString()

        val filesDir = APP_ACTIVITY.filesDir
        val photoFile = File(filesDir, "IMG_$imageID.jpg")

        val selStart = mNoteText.selectionStart
        val selEnd = mNoteText.selectionEnd

        val htmlImage = "<img src=\"$imageID\"/>"

        val text = mNoteText.text.replace(selStart, selEnd, htmlImage)

        mNoteText.text = text

        val uri = FileProvider.getUriForFile(
            APP_ACTIVITY,
            "com.stgroup.android.enote.fileprovider",
            photoFile
        )

        CropImage.activity().setOutputUri(uri).start(APP_ACTIVITY, this)
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
        mInsertImageButton = add_image_button

        mBottomSheetBehaviorTheme = BottomSheetBehavior.from(bottom_sheet_theme)
        mBottomSheetBehaviorTheme.state = BottomSheetBehavior.STATE_HIDDEN

        mBottomSheetBehaviorRichText = BottomSheetBehavior.from(bottom_sheet_rich_text)
        mBottomSheetBehaviorRichText.state = BottomSheetBehavior.STATE_HIDDEN

        // Обьяляем тему пустой. Потом при загрузке замеки делаем проверку
        mCurrentThemeName = ""
    }


    @SuppressLint("SdCardPath")
    private fun loadNote() {
        // Устанавливаем текст в поле для ввода
        // Загружаем из Html, потому что сохраняли в этом формате

        // Если версия андроид больше или равна 24, то разрешаем отображение картинок
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            // Получаем сохраненный текст
            val text = Html.fromHtml(mNote.text, Html.FROM_HTML_MODE_COMPACT)
            // Преобразоываем HTML
            mNoteText.setText(
                Html.fromHtml(
                    text.toString(),
                    Html.FROM_HTML_MODE_COMPACT,
                    { source ->
                        val path = "/data/data/com.stgroup.enote/files/IMG_$source.jpg"
                        // Получаем изображение из пути
                        val drawable = BitmapDrawable.createFromPath(path) as Drawable
                        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                        drawable
                    },
                    null
                )
            )
        } else {
            // Если версия андроид меньше, изобрвжения не подгружаем
            val text = Html.fromHtml(mNote.text)
            mNoteText.setText(Html.fromHtml(text.toString()))
            // Блокируем кнопку
            mInsertImageButton.isClickable = false
        }

        // Проверяем на наличие корректной темы (бежим по списку тем и ищем совпадение в названии)
        mThemeList.forEach {
            if (it.themeName == mNote.background) {
                mDataContainer.background = it.themeImage
                mCurrentThemeName = mNote.background
                mNoteText.setTextColor(getThemeTextColour(mCurrentThemeName))
            }
        }
        // Заголовок на тулбаре
        APP_ACTIVITY.title = mNote.name
        isNoteLoad = true
    }

    private fun saveNote() {
        // Сохраняем текст в Html чтобы сохранить стилизацию
        val text = mNoteText.text

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            mNote.text = Html.toHtml(text, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
        } else {
            mNote.text = Html.toHtml(text)
        }

        if (mCurrentThemeName.isNotEmpty())
            mNote.background = mCurrentThemeName

        // Сохраняем NoteModel в строку джсон
        val jsonString = Gson().toJson(mNote)
        // Сохраняем json в хранилище заметок
        NOTES_STORAGE.edit().putString("$STORAGE_NOTES_ID:${mNote.id}", jsonString).apply()
        isNoteLoad = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val uri = CropImage.getActivityResult(data).uri
                    APP_ACTIVITY.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
            }
        }
    }

}