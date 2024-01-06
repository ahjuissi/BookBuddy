package com.example.bookbuddy.searchView

import org.json.JSONObject

data class BookRVModal(
    // creating string, int and array list
    // variables for our book details
    var title: String,
    var subtitle: String,
    var authors: ArrayList<String>,
    var id: String,
    var publisher: String,
    var publishedDate: String,
    var description: String,
    var pageCount: Int,
    var thumbnail: String,
    var previewLink: String,
    var infoLink: String,
    var buyLink: String
)
