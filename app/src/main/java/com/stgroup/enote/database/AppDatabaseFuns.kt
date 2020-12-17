package com.stgroup.enote.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.stgroup.enote.models.CategoryModel
import com.stgroup.enote.models.NoteModel
import com.stgroup.enote.models.UserModel
import com.stgroup.enote.screens.main_menu_screen.MainMenuFragment
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

fun deleteCategoriesInDatabase(mutableList: MutableList<CategoryModel>) {
    mutableList.forEach {
        deleteCategoryInDatabase(it)
    }
}

fun deleteCategoryInDatabase(category: CategoryModel) {
    val ref = DATABASE.collection(COLLECTION_USERS).document(CURRENT_UID)
        .collection(COLLECTION_CATEGORIES)
    MainMenuFragment.noteList.forEach {
        if (it.categoryId == category.id) {
            deleteNoteFromDatabase(it)
        }
    }
    ref.document(category.id).delete().addOnFailureListener {
        APP_ACTIVITY.showToast(it.message.toString())
    }
}

fun saveNotesToDatabase(noteList: MutableList<NoteModel>) {
    val ref = DATABASE.collection(COLLECTION_USERS).document(CURRENT_UID).collection(
        COLLECTION_CATEGORIES
    )

    noteList.forEach {
        if (!it.inTrash) {
            val dataMap = mutableMapOf<String, Any>()
            dataMap[FIELD_ID] = it.id
            dataMap[FIELD_NAME] = it.name
            dataMap[FIELD_CATEGORY_ID] = it.categoryId
            dataMap[FIELD_TEXT] = it.text
            dataMap[FIELD_DATE_OF_CREATE] = it.dateOfCreate
            dataMap[FIELD_DATE_OF_CHANGE] = it.dateOfChange
            dataMap[FIELD_DATE_TO_COMPLETE] = it.dateToComplete
            dataMap[FIELD_BACKGROUND] = it.background
            dataMap[FIELD_IN_TRASH] = it.inTrash
            ref.document(it.categoryId).collection(COLLECTION_NOTES).document(it.id).set(dataMap)
                .addOnFailureListener {
                    APP_ACTIVITY.showToast(it.message.toString())
                }
        }
    }

}

fun deleteNotesFromDatabase(noteList: MutableList<NoteModel>) {
    noteList.forEach {
        deleteNoteFromDatabase(it)
    }
}

fun deleteNoteFromDatabase(noteModel: NoteModel) {
    DATABASE.collection(COLLECTION_USERS).document(CURRENT_UID)
        .collection(COLLECTION_CATEGORIES).document(noteModel.categoryId).collection(
            COLLECTION_NOTES
        ).document(noteModel.id).delete().addOnFailureListener {
            APP_ACTIVITY.showToast(it.message.toString())
        }
}

fun synchronizeNotes(onSuccess: (MutableList<NoteModel>) -> Unit) {
    val downloadList = mutableListOf<NoteModel>()
    val ref = DATABASE.collection(COLLECTION_USERS).document(CURRENT_UID)
        .collection(COLLECTION_CATEGORIES)
    MainMenuFragment.categoryList.forEach {
        ref.document(it.id).collection(COLLECTION_NOTES).get().addOnSuccessListener {
            val notesArray = it.documents
            notesArray.forEach { docSnap ->
                val dataMap = docSnap.data
                if (!dataMap.isNullOrEmpty()) {
                    val noteModel = NoteModel(
                        dataMap[FIELD_ID].toString(),
                        dataMap[FIELD_NAME].toString(),
                        dataMap[FIELD_CATEGORY_ID].toString(),
                        dataMap[FIELD_TEXT].toString(),
                        dataMap[FIELD_DATE_OF_CREATE].toString(),
                        dataMap[FIELD_DATE_OF_CHANGE].toString(),
                        dataMap[FIELD_DATE_TO_COMPLETE].toString(),
                        dataMap[FIELD_BACKGROUND].toString(),
                        dataMap[FIELD_IN_TRASH].toString().toBoolean(),
                    )
                    downloadList.add(noteModel)
                }
            }
        }.addOnFailureListener {
            APP_ACTIVITY.showToast(it.message.toString())
        }
    }
    onSuccess(downloadList)
}