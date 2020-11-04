package com.stgroup.enote.screens.main_menu_screen

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.stgroup.enote.R
import com.stgroup.enote.models.CategoryModel
import com.stgroup.enote.utilities.APP_ACTIVITY
import com.stgroup.enote.utilities.CATEGORIES_STORAGE
import com.stgroup.enote.utilities.STORAGE_CATEGORIES_ID
import kotlinx.android.synthetic.main.fragment_main_menu.*

class MainMenuFragment : Fragment(R.layout.fragment_main_menu) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MainAdapter

    companion object {
        private var categoryList: MutableList<CategoryModel> = mutableListOf()
    }

    override fun onStart() {
        super.onStart()
        if (categoryList.isEmpty())
            initCategories()
        initRecyclerView()
    }

    private fun initCategories() {
        for (key: String in CATEGORIES_STORAGE.all.keys) {
            val json = CATEGORIES_STORAGE.getString(key, "")
            val categoryModel = Gson().fromJson(json, CategoryModel::class.java)
            categoryList.add(categoryModel)
        }
        if (categoryList.isEmpty()) {
            categoryList.add(CategoryModel(0, "Today"))
            categoryList.add(CategoryModel(1, "Tomorrow"))
            categoryList.add(CategoryModel(2, "Unsorted"))
        }
        categoryList.sortBy {
            it.id
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
    }

    private fun saveCategories() {
        categoryList.forEach { categoryModel ->
            val jsonObject = Gson().toJson(categoryModel)
            CATEGORIES_STORAGE.edit()
                .putString("$STORAGE_CATEGORIES_ID:${categoryModel.id}", jsonObject).apply()
        }
    }
}