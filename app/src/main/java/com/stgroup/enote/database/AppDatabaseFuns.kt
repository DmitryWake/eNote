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

fun initUser() {
    DATABASE.collection(COLLECTION_USERS).document(CURRENT_UID).get().addOnSuccessListener {
        val dataMap = it
        USER.id = dataMap[FIELD_ID].toString()
        USER.phoneNumber = dataMap[FIELD_PHONE].toString()
    }.addOnFailureListener {
        APP_ACTIVITY.showToast(it.message.toString())
    }
}

fun signIn(phoneNumber: String) {
    val uid = AUTH.currentUser?.uid.toString()
    DATABASE.collection(COLLECTION_USERS).document(uid).set(UserModel(uid, phoneNumber))
        .addOnSuccessListener {
            APP_ACTIVITY.showToast("Вы авторизованы!")
            initUser()
        }.addOnFailureListener {
            APP_ACTIVITY.showToast(it.message.toString())
        }
}

fun sighOut() {
    AUTH.signOut()
}