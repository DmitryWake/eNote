package com.stgroup.enote.screens.main_menu_screen

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stgroup.enote.R
import com.stgroup.enote.models.CategoryModel
import com.stgroup.enote.utilities.APP_ACTIVITY
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
        categoryList.add(CategoryModel("Today"))
        categoryList.add(CategoryModel("Tomorrow"))
        categoryList.add(CategoryModel("Unsorted"))
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
}