package com.example.bookbuddy.homeView

class ModelComment {
    var cId: String? = null
    var comment: String? = null
    var ptime: String? = null
    var udp: String? = null
    var uemail: String? = null
    var uid: String? = null
    var uname: String? = null

    constructor()

    constructor(
        cId: String?,
        comment: String?,
        ptime: String?,
        udp: String?,
        uemail: String?,
        uid: String?,
        uname: String?
    ) {
        this.cId = cId
        this.comment = comment
        this.ptime = ptime
        this.udp = udp
        this.uemail = uemail
        this.uid = uid
        this.uname = uname
    }
}
