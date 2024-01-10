package com.example.bookbuddy.searchView

import java.util.Date

data class BookDetailsRVModel(
    var title: String,
    var id: String,
    val olid: String,
    var authors: ArrayList<String>,
    var description: String,
    var subjects: String,
    var subjectPeople: String,
    var subjectTimes: String,
    var publishedDate: String

)