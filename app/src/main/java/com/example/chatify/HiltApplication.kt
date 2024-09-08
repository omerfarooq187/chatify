package com.example.chatify

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication :Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )

        FirebaseMessaging.getInstance().token.addOnCompleteListener {task->
            if(!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result
            sendTokenToServer(token)
        }
    }

    private fun sendTokenToServer(token:String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val tokenData = mapOf(
            "fcmToken" to token
        )

        if (userId != null) {
            FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .setValue(tokenData)
                .addOnSuccessListener {
                    Log.d("Token", "Token successfully add to firebase")
                }
                .addOnFailureListener { e ->
                    Log.d("Token", "$e")
                }
        }
    }
}