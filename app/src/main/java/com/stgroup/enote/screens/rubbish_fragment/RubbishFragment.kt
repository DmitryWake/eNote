package com.stgroup.enote.screens.rubbish_fragment

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        APP_ACTIVITY.menuInflater.inflate(R.menu.rubbish_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.empty_trash -> emptyTrash()
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        initFunctions()
        initNoteList()
        initRecyclerView()
        setHasOptionsMenu(true)
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
        for (key: String in NOTES_STORAGE.all.keys) {
            val json = NOTES_STORAGE.getString(key, "")
            val note = Gson().fromJson(json, NoteModel::class.java)
            // Если категория заметки совпадает с текущей, то добавляем её в наш список
            if (note.inTrash)
                mNoteList.add(note)
        }
        mNoteList.sortBy { it.id }

    }

    private fun emptyTrash(){
        APP_ACTIVITY.showToast("- Знаете как будут звать дочку Ксении Собчак?\n" +
                "- Как?\n" +
                "- Пони.\n")
    }

    private fun saveNoteList() {
        mNoteList.forEach {
            val json = Gson().toJson(it)
            NOTES_STORAGE.edit().putString("$STORAGE_NOTES_ID:${it.id}", json).apply()
        }
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Rubbish"
        APP_ACTIVITY.mDrawer.enableDrawer()
        hideKeyboard()
    }

    override fun onPause() {
        super.onPause()
        saveNoteList()
    }
}
