package com.stgroup.enote.screens.main_menu_screen

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.stgroup.enote.R
import com.stgroup.enote.database.CURRENT_UID
import com.stgroup.enote.database.deleteAllCategories
import com.stgroup.enote.database.saveCategoriesToDatabase
import com.stgroup.enote.database.synchronizeCategories
import com.stgroup.enote.models.CategoryModel
import com.stgroup.enote.utilities.APP_ACTIVITY
import com.stgroup.enote.utilities.CATEGORIES_STORAGE
import com.stgroup.enote.utilities.STORAGE_CATEGORIES_ID
import kotlinx.android.synthetic.main.fragment_main_menu.*
import java.util.*

class MainMenuFragment : Fragment(R.layout.fragment_main_menu) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MainAdapter

    companion object {
        var categoryList: MutableList<CategoryModel> = mutableListOf()
    }

    override fun onStart() {
        super.onStart()
        if (categoryList.isEmpty())
            initCategories()
        initRecyclerView()
        initFunctions()
        if (CURRENT_UID != "null") {
            synchronizeCategories {
                compareLists(it)
            }
        }
    }

    private fun compareLists(it: MutableList<CategoryModel>) {
        if (!it.isNullOrEmpty()) {
            it.sortBy { it.priority }
            if (categoryList.isNotEmpty()) {
                var isEquals = true
                var title =
                    "Список категорий в облаке отличается от вашего\nОтличающиеся категории: "
                it.forEach { categoryModel ->
                    if (!categoryList.contains(categoryModel)) {
                        title += "${categoryModel.name}, "
                        isEquals = false
                    }
                }
                categoryList.forEach { categoryModel ->
                    if (!it.contains(categoryModel)) {
                        title += "${categoryModel.name}, "
                        isEquals = false
                    }
                }
                if (!isEquals) {
                    AlertDialog.Builder(APP_ACTIVITY)
                        .setTitle(title)
                        .setPositiveButton("Обновить") { _, _ ->
                            categoryList = it
                            mAdapter.updateData(categoryList)
                        }
                        .setNegativeButton("Отмена") { _, _ ->
                            deleteAllCategories(it)
                        }
                        .show()
                }

            }
        }
    }

    private fun initFunctions() {
        main_menu_btn_add.setOnClickListener {
            addCategory()
        }
    }

    private fun addCategory() {

        var categoryName = ""
        var priority = 3
        val dialogView = LayoutInflater.from(APP_ACTIVITY).inflate(R.layout.dialog_create_category, null)
        dialogView.findViewById<EditText>(R.id.input_name).addTextChangedListener(object :
            TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                categoryName = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        with(dialogView.findViewById<NumberPicker>(R.id.input_priority)){
            maxValue = 20
            minValue = 0
            value = priority
            setOnValueChangedListener { _, _, newVal ->
                priority = newVal
            }
        }

        AlertDialog.Builder(APP_ACTIVITY)
            .setTitle(R.string.create_category_title)
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                categoryList.add(CategoryModel(UUID.randomUUID().toString(), categoryName, priority))
                categoryList.sortBy { it.priority }
                mAdapter.updateData(categoryList)
            }
            .show()
    }

    private fun initCategories() {
        for (key: String in CATEGORIES_STORAGE.all.keys) {
            val json = CATEGORIES_STORAGE.getString(key, "")
            val categoryModel = Gson().fromJson(json, CategoryModel::class.java)
            categoryList.add(categoryModel)
        }
        if (categoryList.isEmpty()) {
            categoryList.add(CategoryModel("0", "Today", 1))
            categoryList.add(CategoryModel("1", "Tomorrow", 2))
            categoryList.add(CategoryModel("2", "Unsorted", 3))
        }
        categoryList.sortBy {
            it.priority
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = main_menu_recycler_view
        mAdapter = MainAdapter(categoryList)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = GridLayoutManager(APP_ACTIVITY, 2)
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "eNote"
        APP_ACTIVITY.mDrawer.enableDrawer()
    }

    override fun onPause() {
        super.onPause()
        saveCategories()
        if (CURRENT_UID != "null")
            saveCategoriesToDatabase(categoryList)
    }

    private fun saveCategories() {
        categoryList.forEach { categoryModel ->
            val jsonObject = Gson().toJson(categoryModel)
            CATEGORIES_STORAGE.edit()
                .putString("$STORAGE_CATEGORIES_ID:${categoryModel.id}", jsonObject).apply()
        }
    }
}