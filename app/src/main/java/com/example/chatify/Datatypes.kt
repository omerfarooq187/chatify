package com.example.chatify


data class UserData(
    var uid:String?="",
    var name:String?="",
    var email:String?="",
    var number:String?="",
    var imageUrl:String?=""
)

data class ChatMessage(
    val sender:String,
    val message:String
)