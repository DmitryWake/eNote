package com.stgroup.enote.models

data class NoteModel(
    var name: String = "",
    var category: String = "",
    var text: String = "",
    var dateOfCreate: String = "",
    var dateOfChange: String = "",
    var dateToComplete: String = ""
)