package com.stgroup.enote.screens.category_fragment

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.stgroup.enote.R
import com.stgroup.enote.database.CURRENT_UID
import com.stgroup.enote.database.deleteCategoryInDatabase
import com.stgroup.enote.database.saveCategoriesToDatabase
import com.stgroup.enote.database.saveNotesToDatabase
import com.stgroup.enote.models.CategoryModel
import com.stgroup.enote.models.NoteModel
import com.stgroup.enote.screens.main_menu_screen.MainMenuFragment
import com.stgroup.enote.utilities.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.toolbar_search.view.*
import java.util.*
import java.util.concurrent.TimeUnit

class CategoryFragment(private var category: CategoryModel) : Fragment(R.layout.fragment_category) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: CategoryAdapter

    private lateinit var mNoteList: MutableList<NoteModel>

    private var isCategoryDeleted = false

    private lateinit var toolbarEditText: EditText
    private lateinit var toolbarClearButton: ImageView

    private var searchList = listOf<NoteModel>()
    private lateinit var searchTextObservable: Observable<String>

    private fun initViews() {
        toolbarClearButton = APP_ACTIVITY.mToolbar.search_toolbar.clear_icon
        toolbarEditText = APP_ACTIVITY.mToolbar.search_toolbar.search_name_edit_text
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        APP_ACTIVITY.menuInflater.inflate(R.menu.category_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.rename_category -> renameCategory()
            R.id.delete_category -> deleteCategory()
            R.id.change_priority -> changePriority()
        }
        return true
    }

    private fun createTextChangeObservable(): Observable<String> {
        val textChangeObservable = Observable.create<String> { emitter ->
            toolbarEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.toString().let { emitter.onNext(it!!) }
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s != null) {
                        if (s.isNotEmpty()) {
                            toolbarClearButton.visibility = View.VISIBLE
                        } else {
                            toolbarClearButton.visibility = View.INVISIBLE
                        }
                    }
                }

            })
        }
        return textChangeObservable.debounce(1000, TimeUnit.MILLISECONDS)
    }

    private fun renameCategory() {

        var newCategoryName = ""

        val dialogView = LayoutInflater.from(APP_ACTIVITY).inflate(R.layout.dialog_rename, null)
        dialogView.findViewById<EditText>(R.id.input_new_name)
            .addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    newCategoryName = s.toString()
                }

                override fun afterTextChanged(s: Editable?) {}
            })


        AlertDialog.Builder(APP_ACTIVITY)
            .setTitle(R.string.edit_name_title)
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                APP_ACTIVITY.title = newCategoryName
                category.name = newCategoryName
            }
            .show()
    }

    private fun deleteCategory() {
        mNoteList.forEach {
            it.categoryId = "2" // Unsorted
            it.inTrash = true
            MainMenuFragment.noteList.remove(it)
        }
        MainMenuFragment.categoryList.remove(category)
        CATEGORIES_STORAGE.edit().remove("$STORAGE_CATEGORIES_ID:${category.id}").apply()
        isCategoryDeleted = true
        if (CURRENT_UID != "null") {
            deleteCategoryInDatabase(category)
        }
        fragmentManager?.popBackStack()
    }

    private fun changePriority() {

        var newPriority = category.priority

        val dialogView =
            LayoutInflater.from(APP_ACTIVITY).inflate(R.layout.dialog_change_priority, null)
        with(dialogView.findViewById<NumberPicker>(R.id.number_picker)) {
            maxValue = 20
            minValue = 0
            value = newPriority
            setOnValueChangedListener { _, _, newVal ->
                newPriority = newVal
            }
        }

        AlertDialog.Builder(APP_ACTIVITY)
            .setTitle(R.string.change_priority_title)
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                category.priority = newPriority
                MainMenuFragment.categoryList.sortBy { it.priority }
            }
            .show()
    }

    @SuppressLint("CheckResult")
    override fun onStart() {
        super.onStart()
        initViews()
        initFunctions()
        initNoteList()
        initRecyclerView()

        setHasOptionsMenu(true)

        searchTextObservable = createTextChangeObservable()
        searchTextObservable.observeOn(AndroidSchedulers.mainThread())
            .observeOn(Schedulers.io())
            .map { SEARCH_ENGINE.search(it, category.id) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                searchList = it
                println("Succesfylly!!")
            }
    }

    override fun onStop() {
        super.onStop()
        toolbarEditText.addTextChangedListener(null)
    }

    private fun initFunctions() {
        category_btn_add.setOnClickListener {
            addNote()
        }

        toolbarClearButton.setOnClickListener {
            toolbarEditText.editableText.delete(0, toolbarEditText.text.length)
            toolbarClearButton.visibility = View.INVISIBLE
        }
    }

    // Временно
    private fun addNote() {
        val note = NoteModel(
            UUID.randomUUID().toString(),
            "New note",
            category.id,
            dateOfCreate = getFormattedCurrentDate()
        )
        mNoteList.add(note)
        MainMenuFragment.noteList.add(note)
        mAdapter.updateData(mNoteList)
    }

    override fun onPause() {
        super.onPause()
        APP_ACTIVITY.mToolbar.search_toolbar.visibility = View.GONE
        saveNoteList()
        if (!isCategoryDeleted)
            saveCategory()
        if (CURRENT_UID != "null") {
            saveCategoriesToDatabase(mutableListOf(category))
            saveNotesToDatabase(mNoteList)
        }
    }

    private fun saveNoteList() {
        // Сохраняем все заметки из этой категории
        // выполняются лишние действия, потому что, может, что их сохранять и не нужно
        // Поэтому в будущем можно оптимизировать
        mNoteList.forEach {
            val json = Gson().toJson(it)
            NOTES_STORAGE.edit().putString("$STORAGE_NOTES_ID:${it.id}", json).apply()
        }
    }

    private fun saveCategory() {

        val jsonObject = Gson().toJson(category)
        CATEGORIES_STORAGE.edit()
            .putString("$STORAGE_CATEGORIES_ID:${category.id}", jsonObject).apply()
    }

    private fun initRecyclerView() {
        mRecyclerView = category_recycler_view
        mAdapter = CategoryAdapter(mNoteList)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(APP_ACTIVITY)
    }


    // Как вариант загрузить все заметки сразу при запуске приложения, а потом уже отбирать.
    // Нужно подумать над этим вариантом
    private fun initNoteList() {
        mNoteList = mutableListOf()
        MainMenuFragment.noteList.forEach {
            if (it.categoryId == category.id && !it.inTrash)
                mNoteList.add(it)
        }
        mNoteList.sortBy { it.id }
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.mToolbar.search_toolbar.visibility = View.VISIBLE
        APP_ACTIVITY.title = category.name
        hideKeyboard()
    }
}