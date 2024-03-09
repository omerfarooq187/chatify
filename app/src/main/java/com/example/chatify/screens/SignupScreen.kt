package com.example.chatify.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavHostController
import com.example.chatify.CheckSignedIn
import com.example.chatify.CommonProgressBar
import com.example.chatify.DestinationScreen
import com.example.chatify.R
import com.example.chatify.viewModel.MainViewModel

@Composable
fun SignupScreen(viewModel: MainViewModel, navController: NavHostController) {
    CheckSignedIn(viewModel = viewModel, navController = navController)
    SignupScreenContents(viewModel, navController)
}

@Composable
fun SignupScreenContents(viewModel: MainViewModel, navController: NavHostController) {
    var name by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if(viewModel.signIn.value) {
            navController.navigate(DestinationScreen.MainScreen.route)
        }
        Image(
            painter = painterResource(id = R.drawable.chat_bg),
            contentDescription = "background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        )


        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "Create\nAccount",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 45.sp,
                lineHeight = 40.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(24.dp)
            )
            Spacer(modifier = Modifier.height(50.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = {
                        Text(
                            text = "Name",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    },
                    keyboardActions = KeyboardActions(KeyboardActions.Default.onNext),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledTextColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = MaterialTheme
                        .shapes
                        .small
                        .copy(all = CornerSize(20.dp)),
                    modifier = Modifier
                        .padding(12.dp)
                )

                TextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    label = {
                        Text(
                            text = "Email Address",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    },
                    keyboardActions = KeyboardActions(KeyboardActions.Default.onNext),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledTextColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = MaterialTheme
                        .shapes
                        .small
                        .copy(all = CornerSize(20.dp)),
                    modifier = Modifier
                        .padding(12.dp)
                )

                TextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    label = {
                        Text(
                            text = "Password",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    },
                    keyboardActions = KeyboardActions(KeyboardActions.Default.onDone),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledTextColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = MaterialTheme
                        .shapes
                        .small
                        .copy(all = CornerSize(20.dp)),
                    modifier = Modifier
                        .padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = 35.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(24.dp)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = 30.dp)
                        .background(
                            Color("#1F51FF".toColorInt()),
                            shape = RoundedCornerShape(50.dp)
                        )
                        .padding(30.dp)
                        .clickable {
                            viewModel.createUser(name, email, password, context)
                        }
                )
            }
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "Already have an account? Log in",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Blue,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(DestinationScreen.LoginScreen.route) {
                            launchSingleTop = true
                        }
                    }
            )
        }
        if (viewModel.inProcess.value) {
            CommonProgressBar()
        }

    }
}
