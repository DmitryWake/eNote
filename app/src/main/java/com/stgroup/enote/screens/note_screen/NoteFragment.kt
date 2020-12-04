package com.stgroup.enote.screens.note_screen

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.*
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.FileProvider
import androidx.core.view.marginEnd
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

class NoteFragment(private var mNote: NoteModel) : Fragment(R.layout.fragment_note) {

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

        // Основное поле ввода текста
        lateinit var mNoteText: EditText
    }

    // Мэп картинок <lastIndex, ImageSpan>
    private lateinit var mSpans: MutableMap<Int, ImageSpan>

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

    private var isNoteDeleted = false

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        activity?.menuInflater?.inflate(R.menu.note_action_menu, menu)
        if (!mNote.inTrash){
            val restore_item: MenuItem = menu.findItem(R.id.restore_note)
            restore_item.isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_note -> deleteNote()
            R.id.rename_note -> renameNote()
            R.id.restore_note -> restoreNote()
        }
        return true
    }

    private fun restoreNote(){
        mNote.inTrash = false
        fragmentManager?.popBackStack()
    }

    private fun renameNote() {

        var newNoteName = ""

        val dialogView = LayoutInflater.from(APP_ACTIVITY).inflate(R.layout.dialog_rename, null)
        val input : EditText = dialogView.findViewById(R.id.input_new_name)

        input.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                newNoteName = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        AlertDialog.Builder(APP_ACTIVITY)
            .setTitle(R.string.edit_name_title)
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                APP_ACTIVITY.title = newNoteName
                mNote.name = newNoteName }
            .show()
    }

    private fun deleteNote() {
        if (mNote.inTrash) {
            NOTES_STORAGE.edit().remove("$STORAGE_NOTES_ID:${mNote.id}").apply()

            // val spans = mNoteText.text.getSpans(0, mNoteText.text.length, ImageSpan::class.java)

            // Удаляем файлы фотографий, которые не используются
            mSpans.forEach {
                val source = it.value.source
                val path = "/data/data/com.stgroup.enote/files/IMG_$source.jpg"
                val photoFile = File(path)
                photoFile.delete()
            }
            isNoteDeleted = true
        }
        else{
            mNote.inTrash = true
        }
            fragmentManager?.popBackStack()

    }

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

        setHasOptionsMenu(true)
    }

    override fun onPause() {
        super.onPause()
        if (!isNoteDeleted)
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

        val htmlImage = "<img src=\"$imageID\" >"

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
            val text = Html.fromHtml(
                mNote.text,
                Html.FROM_HTML_MODE_LEGACY,
                { source ->
                    val path = "/data/data/com.stgroup.enote/files/IMG_$source.jpg"
                    // Получаем изображение из пути
                    val drawable = BitmapDrawable.createFromPath(path)
                    drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                    drawable
                },
                null
            )

            mNoteText.setText(text)
        } else {
            // Если версия андроид меньше, изобрвжения не подгружаем
            val text = Html.fromHtml(mNote.text)
            mNoteText.setText(text)
            // Блокируем кнопку
            mInsertImageButton.isClickable = false
        }

        // Добавляем картинки в мэп
        mSpans = mutableMapOf()
        val spans = mNoteText.text.getSpans(0, mNoteText.length(), ImageSpan::class.java)
        spans.forEach { span ->
            val index = mNoteText.text.getSpanEnd(span)
            mSpans[index] = span
        }

        // Проверяем на наличие корректной темы (бежим по списку тем и ищем совпадение в названии)
        mThemeList.forEach {
            if (it.themeName == mNote.background) {
                mDataContainer.background = it.themeImage
                mCurrentThemeName = mNote.background
                mNoteText.setTextColor(getThemeTextColour(mCurrentThemeName))
            }
        }

       mDateText.text = if (mNote.dateOfChange.isNotEmpty()) mNote.dateOfChange else mNote.dateOfCreate
        // Заголовок на тулбаре
        APP_ACTIVITY.title = mNote.name
    }

    @SuppressLint("SdCardPath")
    private fun saveNote() {
        // Сохраняем текст в Html чтобы сохранить стилизацию
        var text = mNoteText.text

        // Получаем список изображений
        val spans = text.getSpans(0, text.length, ImageSpan::class.java)

        // Удаляем файлы фотографий, которые не используются
        mSpans.forEach {
            if (text.getSpanEnd(it.value) == -1) {
                val source = it.value.source
                val path = "/data/data/com.stgroup.enote/files/IMG_$source.jpg"
                val photoFile = File(path)
                photoFile.delete()
            }
        }

        // Преобразовываем их в html формат для сохранения
        spans.forEach { span ->
            val indexStart = text.getSpanStart(span)

            val source = span.source
            text.removeSpan(span)
            text = text.replace(indexStart, indexStart + 1, "<img src=\"$source\" >")
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            mNote.text = Html.toHtml(text, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
        } else {
            mNote.text = Html.toHtml(text)
        }

        // Заменяем теги
        mNote.text = mNote.text.replace("&lt;", "<").replace("&gt;", ">")

        if (mCurrentThemeName.isNotEmpty())
            mNote.background = mCurrentThemeName

        mNote.dateOfChange = getFormattedCurrentDate()
        // Сохраняем NoteModel в строку джсон
        val jsonString = Gson().toJson(mNote)
        // Сохраняем json в хранилище заметок
        NOTES_STORAGE.edit().putString("$STORAGE_NOTES_ID:${mNote.id}", jsonString).apply()
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