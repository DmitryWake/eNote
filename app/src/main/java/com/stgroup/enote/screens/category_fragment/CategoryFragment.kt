package com.stgroup.enote.screens.category_fragment

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stgroup.enote.R
import com.stgroup.enote.models.CategoryModel
import com.stgroup.enote.models.NoteModel
import com.stgroup.enote.utilities.APP_ACTIVITY
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryFragment(private var category: CategoryModel) : Fragment(R.layout.fragment_category) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: CategoryAdapter

    private lateinit var mNoteList: MutableList<NoteModel>

    override fun onStart() {
        super.onStart()
        initNoteList()
        initRecyclerView()
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
        mNoteList.add(
            NoteModel(
                "New note1",
                "null",
                "Test",
                "null",
                "26 октября 00:00",
                "null",
                "NightSky"
            )
        )
        mNoteList.add(
            NoteModel(
                "New note2",
                "null",
                "Test",
                "null",
                "26 октября 00:00",
                "null",
                "NightSky"
            )
        )
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = category.name
    }
}