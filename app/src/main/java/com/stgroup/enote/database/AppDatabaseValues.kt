package com.stgroup.enote.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.stgroup.enote.models.UserModel

lateinit var DATABASE: FirebaseFirestore
lateinit var USER: UserModel
lateinit var AUTH: FirebaseAuth
lateinit var CURRENT_UID: String

const val COLLECTION_USERS = "users"

const val FIELD_ID = "id"
const val FIELD_PHONE = "phoneNumber"