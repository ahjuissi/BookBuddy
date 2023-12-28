package com.example.bookbuddy.homeView

class ModelPost(
    var description: String? = null,
    var pid: String? = null,
    var ptime: String? = null,
    var title: String? = null,
    var udp: String? = null,
    var uemail: String? = null,
    var uid: String? = null,
    var uimage: String? = null,
    var uname: String? = null,
    var plike: String? = null,
    var pcomments: String? = null
) {
    constructor() : this("", "", "", "", "", "", "", "", "", "", "")
}
