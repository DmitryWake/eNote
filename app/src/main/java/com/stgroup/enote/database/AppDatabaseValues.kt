package com.stgroup.enote.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.stgroup.enote.models.UserModel

lateinit var DATABASE: FirebaseFirestore
lateinit var USER: UserModel
lateinit var AUTH: FirebaseAuth
lateinit var CURRENT_UID: String

const val COLLECTION_USERS = "users"
const val COLLECTION_CATEGORIES = "categories"
const val COLLECTION_NOTES = "notes"

const val FIELD_ID = "id"
const val FIELD_PHONE = "phoneNumber"
const val FIELD_NAME = "name"
const val FIELD_PRIORITY = "priority"