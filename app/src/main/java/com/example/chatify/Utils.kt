package com.example.chatify

import android.app.Activity
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
import com.example.chatify.viewModel.MainViewModel

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