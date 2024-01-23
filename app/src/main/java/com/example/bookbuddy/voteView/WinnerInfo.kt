package com.example.bookbuddy.voteView

data class WinnerInfo(
    val userId: String = "",
    val city: String = "",
    val bookId: String="",
    val totalVotes: Int=0,
    val bookTitle: String="",
    val thumbnail: String="",
){
    constructor() : this("", "", "", 0,"")
}
