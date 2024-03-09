package com.example.chatify


data class UserData(
    var name:String?="",
) {
    val toMap = hashMapOf(
        "name" to name,
    )
}