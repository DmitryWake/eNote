package com.stgroup.enote.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.stgroup.enote.models.UserModel
import com.stgroup.enote.utilities.APP_ACTIVITY
import com.stgroup.enote.utilities.showToast

fun initFirebase() {
    DATABASE = FirebaseFirestore.getInstance()
    AUTH = FirebaseAuth.getInstance()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
    USER = UserModel()
}

fun initUser(onSuccess: () -> Unit) {
    DATABASE.collection(COLLECTION_USERS).document(CURRENT_UID).get().addOnSuccessListener {
        val datamap = it
        USER.id = datamap[FIELD_ID].toString()
        USER.phoneNumber = datamap[FIELD_PHONE].toString()
    }.addOnFailureListener {
        APP_ACTIVITY.showToast(it.message.toString())
    }
}

fun signIn(phoneNumber: String) {
    val uid = AUTH.currentUser?.uid.toString()
    DATABASE.collection(COLLECTION_USERS).document(uid).set(UserModel(uid, phoneNumber))
        .addOnSuccessListener {
            APP_ACTIVITY.showToast("Вы авторизованы!")
            initFirebase()
        }.addOnFailureListener {
        APP_ACTIVITY.showToast(it.message.toString())
    }
}