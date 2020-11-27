package com.stgroup.enote.screens.rubbish_fragment

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.stgroup.enote.R
import com.stgroup.enote.models.NoteModel
import com.stgroup.enote.utilities.*
import kotlinx.android.synthetic.main.fragment_rubbish.*

class RubbishFragment : Fragment(R.layout.fragment_rubbish) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RubbishAdapter

    private lateinit var mNoteList: MutableList<NoteModel>

    override fun onStart() {
        super.onStart()
        initFunctions()
        initNoteList()
        initRecyclerView()
    }

    private fun initFunctions() {}

    private fun initRecyclerView() {
        mRecyclerView = rubbish_recycler_view
        mAdapter = RubbishAdapter(mNoteList)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(APP_ACTIVITY)
    }

    private fun initNoteList() {
        mNoteList = mutableListOf()
        // Загружаем заметки из хранилища
        for (key: String in NOTES_DELETED.all.keys) {
            val json = NOTES_DELETED.getString(key, "")
            val note = Gson().fromJson(json, NoteModel::class.java)

            mNoteList.add(note)

        }
        // mNoteList.sortBy { it.id }

    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "RubbishObama"
        hideKeyboard()
    }
}