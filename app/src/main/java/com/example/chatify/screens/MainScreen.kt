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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
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
import com.example.chatify.viewModel.MainViewModel
import com.example.chatify.ui.theme.appFontFamily
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
                containerColor = Color("#ADD8E6".toColorInt())
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
        }
    ) {
        Box(
            modifier = Modifier
                .background(Color.Gray)
                .padding(it.calculateTopPadding())
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
    var selectedUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            selectedUri = it
        }

    if (!viewModel.signIn.value) {
        // Navigate to the login screen if the user is not signed in
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .background(Color("#ADD8E6".toColorInt()))
                    .padding(18.dp)
            ) {
                Text(
                    text = "Back",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(NavigationScreen.ChatScreen.route) {
                                launchSingleTop = true
                            }
                        }
                )
                Text(
                    text = "Save",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable {
                            if (selectedUri != null) {
                                viewModel.uploadData(selectedUri!!, name, number)
                            } else {
                                viewModel.uploadData(name, number)
                            }
                        }
                )
            }
            Column(
                modifier = Modifier
                    .height(250.dp)
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
                            .height(200.dp)
                            .width(200.dp)
                            .clip(CircleShape)
                    )
                } else if (userData?.imageUrl != null) {
                    AsyncImage(
                        model = userData.imageUrl,
                        contentDescription = "Profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(200.dp)
                            .width(200.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        imageVector = Icons.Default.Person,
                        contentDescription = "photo",
                        modifier = Modifier
                            .height(200.dp)
                            .width(200.dp)
                            .clip(CircleShape)
                    )
                }
                Text(
                    text = "Change profile photo"
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Name",
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(4.dp)
                    )
                    BasicTextField(
                        value = name,
                        onValueChange = {
                            name = it
                        },
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .width(250.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Number",
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(4.dp)
                    )
                    BasicTextField(
                        value = number,
                        onValueChange = {
                            number = it
                        },
                        textStyle = TextStyle(
                            fontSize = 18.sp
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .width(250.dp)
                    )
                }

                Button(
                    onClick = {
                        viewModel.logoutUser()
                    },
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    Text(text = "LOG OUT")
                }
            }
        }
        if (viewModel.inProcess.value) {
            CommonProgressBar()
        }
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
    Scaffold(
        topBar = {
            Column(verticalArrangement = Arrangement.spacedBy((-1).dp)) {
                ProjectsTopAppBar(
                    search = {
                        search = !search
                    }
                )
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
            }

        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(8.dp)
            ) {

                items(users.filterNot {
                    it.uid == viewModel.currentUserId.value
                }) { user ->
                        ChatItem(
                            imageUrl = user.imageUrl,
                            name = user.name,
                            onItemClick = {
                                mainNavController.navigate("${NavigationScreen.MessagesScreen.route}/${user.uid}")
                            }
                        )
                }
            }
        }
    }

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
            containerColor = Color("#ADD8E6".toColorInt()),
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
                .padding(start = 12.dp, top = 2.dp, end = 12.dp, bottom = 12.dp)
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
                it.number == searchQuery
            }) {
                ChatItem(
                    imageUrl = it.imageUrl,
                    name = it.name,
                    onItemClick = {
                        mainNavController.navigate("${NavigationScreen.MessagesScreen.route}/${it.uid}")
                    }
                )
            }
        }
    }
}
@Composable
fun ChatItem(imageUrl: String?, name: String?, onItemClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .clickable {
                onItemClick()
            }
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Profile photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(80.dp)
                    .width(80.dp)
                    .clip(CircleShape)
            )
        } else {
            Image(
                imageVector = Icons.Default.Person,
                contentDescription = "",
                modifier = Modifier
                    .height(80.dp)
                    .width(80.dp)
                    .clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = name!!,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MessagesScreen(
    selectedUserId: String,
    navController: NavHostController,
    viewModel: MainViewModel
) {
    var message by remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val messages = viewModel.messages.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.messages.value = emptyList()
        viewModel.realAllMessages(selectedUserId)
    }
    if (viewModel.inProcess.value) {
        CommonProgressBar()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color("#ADD8E6".toColorInt()))
                    .statusBarsPadding()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Back",
                    style = TextStyle(
                        fontFamily = appFontFamily
                    ),
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable {
                            navController.popBackStack()
                        }
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "New Chat",
                    modifier = Modifier
                        .size(30.dp)
                )
            }
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = lazyListState
                ) {
                    scope.launch {
                        lazyListState.animateScrollToItem(messages.value.size)
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(8.dp)
            ) {
                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        message = it
                    },
                    placeholder = {
                        Text(text = "Type your message")
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            modifier = Modifier
                                .clickable {
                                    viewModel.sendMessage(selectedUserId, message)
                                    message = ""
                                }
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(onSend = {
                        viewModel.sendMessage(selectedUserId, message)
                        message = ""

                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                )
            }
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