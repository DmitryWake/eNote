package com.stgroup.enote.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.stgroup.enote.models.CategoryModel
import com.stgroup.enote.models.NoteModel
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
            CURRENT_UID = uid
            initUser()
        }.addOnFailureListener {
            APP_ACTIVITY.showToast(it.message.toString())
        }
}

fun sighOut() {
    AUTH.signOut()
}

fun saveCategoriesToDatabase(categoryList: MutableList<CategoryModel>) {
    val ref = DATABASE.collection(COLLECTION_USERS).document(CURRENT_UID).collection(
        COLLECTION_CATEGORIES
    )

    categoryList.forEach {
        val dataMap = mutableMapOf<String, String>()
        dataMap[FIELD_ID] = it.id
        dataMap[FIELD_NAME] = it.name
        dataMap[FIELD_PRIORITY] = it.priority.toString()
        ref.document(it.id).set(dataMap).addOnFailureListener {
            APP_ACTIVITY.showToast(it.message.toString())
        }
    }
}

fun synchronizeCategories(onSuccess: (MutableList<CategoryModel>) -> Unit) {
    val downloadList = mutableListOf<CategoryModel>()
    DATABASE.collection(COLLECTION_USERS).document(CURRENT_UID).collection(COLLECTION_CATEGORIES)
        .get().addOnSuccessListener {
            val categoriesArray = it.documents
            categoriesArray.forEach { docSnap ->
                val dataMap = docSnap.data
                if (!dataMap.isNullOrEmpty()) {
                    val categoryModel = CategoryModel(
                        dataMap[FIELD_ID].toString(),
                        dataMap[FIELD_NAME].toString(),
                        dataMap[FIELD_PRIORITY].toString().toInt()
                    )
                    downloadList.add(categoryModel)
                }
            }
            onSuccess(downloadList)
        }.addOnFailureListener {
            APP_ACTIVITY.showToast(it.message.toString())
        }
}

fun deleteAllCategories(mutableList: MutableList<CategoryModel>) {
    val ref = DATABASE.collection(COLLECTION_USERS).document(CURRENT_UID)
        .collection(COLLECTION_CATEGORIES)
    mutableList.forEach {
        ref.document(it.id).delete().addOnFailureListener {
            APP_ACTIVITY.showToast(it.message.toString())
        }
    }
}

fun deleteCategoryInDatabase(category: CategoryModel) {
    val ref = DATABASE.collection(COLLECTION_USERS).document(CURRENT_UID)
        .collection(COLLECTION_CATEGORIES)
    ref.document(category.id).delete().addOnFailureListener {
        APP_ACTIVITY.showToast(it.message.toString())
    }
}

fun saveNotesToDatabase(noteList: MutableList<NoteModel>) {
    val ref = DATABASE.collection(COLLECTION_USERS).document(CURRENT_UID).collection(
        COLLECTION_CATEGORIES
    ).document()
}

fun synchronizeNotes(function: (MutableList<NoteModel>) -> Unit) {
    val downloadList = mutableListOf<NoteModel>()

}