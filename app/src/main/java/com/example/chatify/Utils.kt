package com.example.chatify

import android.app.Activity
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.chatify.retrofit.MessageApi
import com.example.chatify.retrofit.MessageRequest
import com.example.chatify.viewModel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun CommonProgressBar() {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun CheckSignedIn(viewModel: MainViewModel, navController: NavController) {
    val alreadySignedIn by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    DisposableEffect(onBackPressedDispatcher) {
        val callback = onBackPressedDispatcher?.addCallback {
            if (viewModel.signIn.value && !alreadySignedIn) {
                (context as? Activity)?.finish()
            } else {
                navController.popBackStack()
            }
        }
        onDispose {
            callback?.remove()
        }
    }

    if (viewModel.signIn.value && !alreadySignedIn) {
        navController.navigate(DestinationScreen.MainScreen.route) {
            popUpTo(0)
        }
    }
}

fun sendMessageToBackend(userId:String, message:String) {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.116.62:3000/") // Replace with your server URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(MessageApi::class.java)

    val notificationRequest = MessageRequest(userId, message)
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = apiService.sendNotification(notificationRequest)
            if (response.isSuccessful) {
                Log.d("Notification", "Notification request sent successfully")
            } else {
                Log.e("Notification", "Failed to send notification request: ${response.errorBody()}")
            }
        } catch (e: Exception) {
            Log.e("Notification", "Error sending notification request", e)
        }
    }
}