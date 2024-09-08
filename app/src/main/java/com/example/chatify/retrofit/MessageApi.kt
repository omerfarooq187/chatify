package com.example.chatify.retrofit

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MessageApi {
    @POST("sendNotification")
    suspend fun sendNotification(@Body request: MessageRequest): Response<ResponseBody>
}

data class MessageRequest(
    val userId:String,
    val message:String
)