package com.stgroup.enote.screens.category_fragment

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.stgroup.enote.R
import com.stgroup.enote.models.CategoryModel
import com.stgroup.enote.models.NoteModel
import com.stgroup.enote.utilities.*
import kotlinx.android.synthetic.main.fragment_category.*
import java.util.*

class CategoryFragment(private var category: CategoryModel) : Fragment(R.layout.fragment_category) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: CategoryAdapter

    private lateinit var mNoteList: MutableList<NoteModel>

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.category_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.rename_category -> renameCategory()
            R.id.delete_category -> deleteCategory()
            R.id.change_priority -> changePriority()
        }
        return true
    }

    private fun renameCategory() {

        var newCategoryName = ""

        val dialogView = LayoutInflater.from(APP_ACTIVITY).inflate(R.layout.dialog_rename, null)
        val input : EditText = dialogView.findViewById(R.id.input_new_name)

        input.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

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
                category.name = newCategoryName }
            .show()
    }

    private fun deleteCategory() {
        APP_ACTIVITY.showToast("Deleting category")
    }

    private fun changePriority() {
        APP_ACTIVITY.showToast("Changing priority")
    }

    override fun onStart() {
        super.onStart()
        initFunctions()
        initNoteList()
        initRecyclerView()

        setHasOptionsMenu(true)
    }

    private fun initFunctions() {
        category_btn_add.setOnClickListener {
            addNote()
        }
    }

    // Временно
    private fun addNote() {
        mNoteList.add(NoteModel(UUID.randomUUID().toString(), "New note", category.name, dateOfCreate=getFormattedCurrentDate()))
        mAdapter.updateData(mNoteList)
    }

    override fun onPause() {
        super.onPause()
        saveNoteList()
        saveCategory()
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
        // Загружаем заметки из хранилища
        for (key: String in NOTES_STORAGE.all.keys) {
            val json = NOTES_STORAGE.getString(key, "")
            val note = Gson().fromJson(json, NoteModel::class.java)
            // Если категория заметки совпадает с текущей, то добавляем её в наш список
            if (note.category == category.name)
                mNoteList.add(note)
        }
        mNoteList.sortBy { it.id }

        /*if (mNoteList.isEmpty()) {
            mNoteList.add(NoteModel(UUID.randomUUID().toString(), "New note", category.name))
        }*/
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = category.name
        hideKeyboard()
    }
}