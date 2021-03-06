package com.stgroup.enote.utilities

import android.content.SharedPreferences
import com.stgroup.enote.MainActivity
import com.stgroup.enote.objects.SearchEngine

lateinit var APP_ACTIVITY: MainActivity

lateinit var SEARCH_ENGINE: SearchEngine

const val THEMES_FOLDER = "themes"

const val STORAGE_CATEGORIES_NAME: String = "categories"
const val STORAGE_CATEGORIES_ID: String = "category_id"
const val STORAGE_NOTES_NAME: String = "notes"
const val STORAGE_NOTES_ID: String = "note_id"

// Хранилище категорий
lateinit var CATEGORIES_STORAGE: SharedPreferences

// Хранилище заметок
lateinit var NOTES_STORAGE: SharedPreferences
