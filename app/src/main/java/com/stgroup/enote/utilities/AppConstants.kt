package com.stgroup.enote.utilities

import android.content.SharedPreferences
import com.stgroup.enote.MainActivity

lateinit var APP_ACTIVITY: MainActivity

const val THEMES_FOLDER = "themes"

const val STORAGE_CATEGORIES_NAME: String = "categories"
const val STORAGE_CATEGORIES_ID: String = "category_id"
const val STORAGE_NOTES_NAME: String = "notes"
const val STORAGE_NOTES_ID: String = "note_id"

const val DELETED_CATEGORIES_NAME: String = "categories"
const val DELETED_CATEGORIES_ID: String = "category_id"
const val DELETED_NOTES_NAME: String = "notes"
const val DELETED_NOTES_ID: String = "note_id"

// Хранилище категорий
lateinit var CATEGORIES_STORAGE: SharedPreferences

// Хранилище заметок
lateinit var NOTES_STORAGE: SharedPreferences

// Хранилище удаленных заметок
lateinit var NOTES_DELETED: SharedPreferences