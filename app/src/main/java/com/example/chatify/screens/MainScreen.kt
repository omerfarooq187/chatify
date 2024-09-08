package com.example.chatify.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.chatify.CommonProgressBar
import com.example.chatify.DestinationScreen
import com.example.chatify.sendMessageToBackend
import com.example.chatify.ui.theme.appFontFamily
import com.example.chatify.viewModel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(viewModel: MainViewModel, navController: NavHostController) {
    MainScreenContents(viewModel, navController)
}

sealed class NavigationScreen(val route: String) {
    data object ChatScreen : NavigationScreen("chat")
    data object ProfileScreen : NavigationScreen("profile")
    data object MessagesScreen : NavigationScreen("messages")
}

@Composable
fun MainScreenContents(viewModel: MainViewModel, mainNavController: NavHostController) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            BottomAppBar(
                containerColor = Color.White,
                modifier = Modifier
                    .shadow(4.dp)
            ) {
                navigationItems.forEach { navigationItem ->
                    NavigationBarItem(
                        selected = (navigationItem.screen == currentDestination?.route),
                        onClick = {
                            navController.navigate(navigationItem.screen) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = {
                            Text(text = navigationItem.title)
                        },
                        icon = {
                            BadgedBox(badge = {
                                if (navigationItem.badges != null) {
                                    Badge { Text(text = navigationItem.badges.toString()) }
                                }
                            }) {
                                Icon(
                                    imageVector = navigationItem.selectedIcon,
                                    contentDescription = navigationItem.title
                                )
                            }
                        }
                    )
                }
            }
        },
        content = {
            Box(
                modifier = Modifier
            ) {
                NavHost(
                    navController = navController,
                    startDestination = NavigationScreen.ChatScreen.route
                ) {
                    composable(NavigationScreen.ChatScreen.route) {
                        ChatScreen(viewModel, mainNavController)
                    }
                    composable(NavigationScreen.ProfileScreen.route) {
                        ProfileScreen(navController, mainNavController, viewModel)
                    }
                }
            }
        }
    )
}

data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val badges: Int? = null,
    val screen: String
)

val navigationItems = listOf(
    NavigationItem(
        "Chats",
        Icons.AutoMirrored.Filled.Chat,
        Icons.AutoMirrored.Outlined.Chat,
        3,
        NavigationScreen.ChatScreen.route
    ),
    NavigationItem(
        "Profile",
        Icons.Filled.Person,
        Icons.Outlined.Person,
        screen = NavigationScreen.ProfileScreen.route
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    mainNavController: NavHostController,
    viewModel: MainViewModel
) {
    val userData = viewModel.userData.value
    var name by rememberSaveable {
        mutableStateOf(userData?.name ?: "")
    }
    var number by rememberSaveable {
        mutableStateOf(userData?.number ?: "")
    }
    var email by rememberSaveable {
        mutableStateOf(userData?.email ?: "")
    }
    var selectedUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            selectedUri = it
        }

    if (!viewModel.signIn.value) {
        mainNavController.navigate(DestinationScreen.SignupScreen.route) {
            launchSingleTop = true
            restoreState = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(NavigationScreen.ChatScreen.route) {
                            launchSingleTop = true
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Text(
                        text = "Save",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                if (selectedUri != null) {
                                    viewModel.uploadData(uri = selectedUri!!, name = name, email = email, number = number)
                                } else {
                                    viewModel.uploadData(name, number, email)
                                }
                            }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    actionIconContentColor = Color.Black
                ),
                modifier = Modifier.shadow(4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Image Section
            Column(
                modifier = Modifier
                    .height(200.dp)
                    .clickable {
                        launcher.launch("image/*")
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (selectedUri != null) {
                    AsyncImage(
                        model = selectedUri,
                        contentDescription = "Profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                    )
                } else if (userData?.imageUrl != null) {
                    AsyncImage(
                        model = userData.imageUrl,
                        contentDescription = "Profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        imageVector = Icons.Default.Person,
                        contentDescription = "photo",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.5f))
                    )
                }
                Text(
                    text = "Change profile photo",
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Editable Fields (Name, Number, Email)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                ProfileField(label = "Name", value = name, onValueChange = { name = it })
                Spacer(modifier = Modifier.height(16.dp))
                ProfileField(label = "Number", value = number, onValueChange = { number = it })
                Spacer(modifier = Modifier.height(16.dp))
                ProfileField(label = "Email", value = email, onValueChange = { email = it })
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.logoutUser()
                },
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(0.5f)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "LOG OUT",
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }
        }

        // Display a progress bar while data is being uploaded
        if (viewModel.inProcess.value) {
            CommonProgressBar()
        }
    }
}

// Custom composable for text fields with labels
@Composable
fun ProfileField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(fontSize = 18.sp),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: MainViewModel,
    mainNavController: NavHostController
) {
    val users = viewModel.usersList.value
    var search by remember {
        mutableStateOf(false)
    }
    val isSearching = viewModel.searching.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)  // Light gray background
    ) {
        // TopAppBar with Search Button
        ProjectsTopAppBar(
            search = {
                search = !search
            },
            modifier = Modifier
                .shadow(4.dp)
        )

        // Search Bar - Visible if search is active
        if (search) {
            EmbeddedSearchBar(
                onQueryChange = viewModel::onSearchTextChange,
                isSearchActive = isSearching.value,
                onActiveChanged = { viewModel.onToggleSearch() },
                onSearch = viewModel::onSearchTextChange,
                viewModel = viewModel,
                mainNavController = mainNavController
            )
        }

        // Chat list
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (users.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(users.filterNot { it.uid == viewModel.currentUserId.value }) { user ->
                        ChatItem(
                            imageUrl = user.imageUrl,
                            name = user.name!!,
                            onItemClick = {
                                mainNavController.navigate("${NavigationScreen.MessagesScreen.route}/${user.uid}")
                            }
                        )
                    }
                }
            } else {
                // Show when no users are found
                Text(
                    text = "No chats available",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    // Show progress indicator if any process is running
    if (viewModel.inProcess.value) {
        CommonProgressBar()
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ProjectsTopAppBar(
    search: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = "Chats",
                style = TextStyle(
                    fontFamily = appFontFamily
                ),
                fontSize = 20.sp
            )
        },
        actions = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "search",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(30.dp)
                    .clickable {
                        search()
                    }
            )
        },
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            actionIconContentColor = Color.Black
        ),
        scrollBehavior = scrollBehavior,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EmbeddedSearchBar(
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: ((String) -> Unit)? = null,
    viewModel: MainViewModel,
    mainNavController: NavHostController
) {
    val users = viewModel.usersList.value
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val activeChanged: (Boolean) -> Unit = { active ->
        searchQuery = ""
        onQueryChange("")
        onActiveChanged(active)
    }
    SearchBar(
        query = searchQuery,
        onQueryChange = { query ->
            searchQuery = query
            onQueryChange(query)
        },
        onSearch = onSearch ?: { activeChanged(false) },
        active = isSearchActive,
        onActiveChange = activeChanged,
        modifier = if (isSearchActive) {
            modifier
                .animateContentSize(spring(stiffness = Spring.StiffnessHigh))
        } else {
            modifier
//                .padding(start = 12.dp, top = 2.dp, end = 12.dp, bottom = 12.dp)
                .fillMaxWidth()
                .animateContentSize(spring(stiffness = Spring.StiffnessHigh))
        },
        placeholder = { Text("Search") },
        leadingIcon = {
            if (isSearchActive) {
                IconButton(
                    onClick = { activeChanged(false) },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        trailingIcon = if (isSearchActive && searchQuery.isNotEmpty()) {
            {
                IconButton(
                    onClick = {
                        searchQuery = ""
                        onQueryChange("")
                    },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        } else {
            null
        },
        colors = SearchBarDefaults.colors(
            containerColor = if (isSearchActive) {
                MaterialTheme.colorScheme.background
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            },
        ),
        tonalElevation = 0.dp,
        windowInsets = if (isSearchActive) {
            SearchBarDefaults.windowInsets
        } else {
            WindowInsets(0.dp)
        }
    ) {
        LazyColumn {
            items(users.filter {
                it.email == searchQuery
            }) {
                ChatItem(
                    imageUrl = it.imageUrl,
                    name = it.name!!,
                    onItemClick = {
                        mainNavController.navigate("${NavigationScreen.MessagesScreen.route}/${it.uid}")
                    }
                )
            }
        }
    }
}

@Composable
fun ChatItem(
    imageUrl: String?,
    name: String,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile image
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "User Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.3f))
            )
        } else {
            // Default icon if no image is available
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Default User Icon",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.3f))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // User name
        Text(
            text = name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    selectedUserId: String,
    navController: NavHostController,
    viewModel: MainViewModel
) {
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val messages = viewModel.messages.collectAsState(initial = emptyList())

    // Reset messages on screen load and read all messages for the selected user
    LaunchedEffect(Unit) {
        viewModel.messages.value = emptyList()
        viewModel.realAllMessages(selectedUserId)
    }

    // Show a progress bar if in process
    if (viewModel.inProcess.value) {
        CommonProgressBar()
    }

    // Main Screen Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(8.dp) // Consistent padding for all screen components
    ) {

        // TopAppBar with a back button
        TopAppBar(
            title = {
                Text(
                    text = "Messages",
                    style = TextStyle(
                        fontFamily = appFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { /* Handle new chat action */ }) {
                    Icon(Icons.Default.Edit, contentDescription = "New Chat")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Message list container
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                state = lazyListState
            ) {
                scope.launch {
                    lazyListState.animateScrollToItem(messages.value.size) // Scroll to last message
                }
                items(messages.value) { message ->
                    MessageContent(
                        sender = message.sender,
                        message = message.message,
                        currentUserId = viewModel.currentUserId.value
                    )
                }
            }
        }

        // Message input field
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp)
                .background(Color(0xFFF6F6F6), RoundedCornerShape(20.dp)) // Rounded background
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = {
                    message = it
                },
                placeholder = {
                    Text(text = "Type your message...")
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color(0xFF6200EE), // Icon color
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                if (message.isNotEmpty()) {
                                    viewModel.sendMessage(selectedUserId, message)
                                    sendMessageToBackend(selectedUserId,message)
                                    message = ""
                                }
                            }
                    )
                },
                shape = RoundedCornerShape(20.dp), // Rounded corners for text field
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(onSend = {
                    if (message.isNotEmpty()) {
                        viewModel.sendMessage(selectedUserId, message)
                        message = ""
                    }
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}


@Composable
fun MessageContent(
    sender: String,
    message: String,
    currentUserId: String,
) {
    val isCurrentUser = sender == currentUserId
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .wrapContentHeight()
                .padding(8.dp),
            colors = CardColors(
                containerColor = if (isCurrentUser) Color.Gray else Color.Green,
                disabledContainerColor = if (isCurrentUser) Color.Gray else Color.Green,
                contentColor = Color.White,
                disabledContentColor = Color.White
            )
        ) {
            Text(
                text = message,
                color = Color.White,
                modifier = Modifier
                    .padding(8.dp)
            )
        }
    }
}