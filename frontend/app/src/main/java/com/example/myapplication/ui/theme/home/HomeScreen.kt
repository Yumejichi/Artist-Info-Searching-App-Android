package com.example.myapplication.ui.theme.home

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.example.myapplication.Routes
import com.example.myapplication.model.auth.FavoriteArtist
import com.example.myapplication.model.auth.UserData
import com.example.myapplication.ui.theme.auth.AuthViewModel
import com.example.myapplication.ui.theme.search.SearchScreen
import com.example.myapplication.ui.theme.search.SearchViewModel
import java.time.Instant
import androidx.compose.foundation.lazy.items
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.LocalTextStyle
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(authViewModel: AuthViewModel,
               searchViewModel: SearchViewModel,
               navController: NavController,
               isLoggedIn: Boolean,
               userData: UserData?) {
    val searchIconState by searchViewModel.searchIconState
    val searchTextState by searchViewModel.searchTextState
    val scope = rememberCoroutineScope()
    val isLoggedIn by authViewModel.isLoggedIn
    val wasLoggedIn = remember { mutableStateOf(isLoggedIn) }

    LaunchedEffect(isLoggedIn) {
        if (wasLoggedIn.value && !isLoggedIn) {
            navController.navigate(Routes.homeScreen) {
                popUpTo(Routes.homeScreen) { inclusive = true }
            }
        }
        wasLoggedIn.value = isLoggedIn
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Manually placed App Bar at top
        HomeAppBar(
            searchIconState = searchIconState,
            searchTextState = searchTextState,
            onQueryChange = { searchViewModel.onQueryChange(it){} },
            onCloseClick = {
//                searchViewModel.onCloseIconClicked()
//                searchViewModel.updateSearchIconState(SearchIconState.CLOSED)
//                searchViewModel.updateSearchIconState(SearchIconState.CLOSED)
//                searchViewModel.updateSearchTextState("")
                searchViewModel.onCloseIconClicked()

            },
            onSearch = { query ->
                searchViewModel.onQueryChange(query) {
                    println("Search submitted: $query")
                }
            },
            onSearchIconClick = { searchViewModel.onSearchIconClicked() },
            onSearchTriggered = {
                searchViewModel.onSearchIconClicked()
                searchViewModel.updateSearchIconState(SearchIconState.OPENED)
            },
            isLoggedIn,
            userData = userData,
            onPersonIconClick  = { navController.navigate(Routes.loginScreen) },
            onLogout = { authViewModel.logout(scope){ res->
                        if (res) {
                            navController.navigate(Routes.homeScreen) {
                                popUpTo(Routes.homeScreen) { inclusive = true }
                            }
                        }
                }
            },
            onDeleteAccount = { authViewModel.deleteAccount(scope){ res->
                if (res) {
                    navController.navigate(Routes.homeScreen) {
                        popUpTo(Routes.homeScreen) { inclusive = true }
                    }
                }
            }
            }


        )

        if (searchIconState == SearchIconState.CLOSED) {
            ShowHomeContent(
                navController = navController, isLoggedIn = isLoggedIn, userData = userData
            )
        } else {
            SearchScreen(isLoggedIn = isLoggedIn, viewModel = searchViewModel, authViewModel = authViewModel, query = searchTextState,onCardClick = { artistId ->
                navController.navigate("artist_detail_screen/$artistId")
            })
        }
    }
    }

@Composable
fun HomeAppBar(
    searchIconState: SearchIconState,
    searchTextState: String,
    onQueryChange: (String) -> Unit,
    onCloseClick: () -> Unit,
    onSearch: (String) -> Unit,
    onSearchIconClick: () -> Unit,
    onSearchTriggered: () -> Unit,
    isLoggedIn: Boolean,
    userData: UserData?,
    onPersonIconClick: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
){
    if (searchIconState == SearchIconState.OPENED) {
//        ArtistSearchBar(
//            state = searchIconState,
//            searchTextState = searchTextState,
//            onQueryChange = onQueryChange,
//            onCloseClick = onCloseClick,
//            onSearch = onSearch
//        )
        SearchTopBar(
            query = searchTextState,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            onCloseClick = onCloseClick
        )

    } else {
        TopBar(
            onSearchClicked = onSearchTriggered,
            isLoggedIn = isLoggedIn,
            userData = userData,
            onPersonIconClick = onPersonIconClick,
            onLogout = onLogout,
            onDeleteAccount = onDeleteAccount
        )
    }

}
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreenBackUp() {
//
//    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
//
//    val navController = rememberNavController()
////            NavHost(navController = navController, startDestination = "home_screen", builder = {
////                composable("register_screen") {
////                    RegisterScreen(navController)
////                }
////                composable("login_screen") {
////                    LoginScreen(navController)
////                }
////            })
//
//    val viewModel: SearchViewModel = viewModel()
//    val state = viewModel.state
//
//    MyApplicationTheme { NavHost(navController = navController, startDestination = Routes.homeScreen) {
//        composable(Routes.homeScreen) {
//            Scaffold (
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.White),
//                topBar = {
//                    if (state.isBarVisible) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(64.dp)
//                                .background(MaterialTheme.colorScheme.primaryContainer)
//
//                        ) {
//                            ArtistSearchBar(
//                                state = state,
//                                onQueryChange = viewModel::updateQuery,
//                                onCloseClick = { viewModel.onUserAction(UserAction.CloseClicked) },
//                                onSearch = { /* Handle search logic */ },
//                                modifier = Modifier.fillMaxWidth().height(64.dp)
//                            )
//                        }
//                    } else {
//                        TopAppBar(
////                                onSearchClick = {
////                                  viewModel.onUserAction(UserAction.SearchClicked)
////                                }
//                            colors = TopAppBarDefaults.topAppBarColors(
//                                containerColor = MaterialTheme.colorScheme.primaryContainer
//                            ),
//                            title = {
//                                Text(text = "Artist Search")
//                            },
//                            actions = {
//                                IconButton(onClick = { viewModel.onUserAction(UserAction.SearchClicked) }) {
//                                    Icon(
//                                        imageVector = Icons.Outlined.Search,
//                                        contentDescription = "search"
//                                    )
//                                }
//                                IconButton(onClick = { /* do something */ }) {
//                                    Icon(
//                                        imageVector = Icons.Outlined.Person,
//                                        contentDescription = "account"
//                                    )
//                                }
//                            },
//                        )
//
//                    }
//                }
//            ) { innerPadding ->
////                    Greeting(
////                        name = "Android",
////                        modifier = Modifier.padding(innerPadding)
////                    )
//                if (!state.isBarVisible) {
//                    Column(modifier = Modifier.padding(innerPadding)) {
//                        ShowHomeContent(navController = navController)
//                    }
//                }
//
//            }
//        }
//
//
//        composable(Routes.loginScreen) {
//            LoginScreen(navController)
//        }
//
//        composable(Routes.registerScreen) {
//            RegisterScreen(navController)
//        }
//    }
//    }
//}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onSearchClicked: () -> Unit, isLoggedIn: Boolean, userData: UserData?, onPersonIconClick: () -> Unit, onLogout:()->Unit, onDeleteAccount:()->Unit) {
    var expandDropdownMenu by remember { mutableStateOf(false) }
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        title = {
            Text(text = "Artist Search")
        },
        actions = {
            IconButton(onClick = { onSearchClicked() }) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search Icon"
                )
            }
            if (!isLoggedIn) {
                IconButton(onClick = onPersonIconClick) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Person Icon"
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                ) {

                    AsyncImage(
                        model = userData?.profileImageUrl,
                        contentDescription = "user profile image",
                        modifier = Modifier.size(30.dp).clip(CircleShape)
                            .clickable{
                                expandDropdownMenu = true
                            },
                        contentScale = ContentScale.Crop
                    )
                    DropdownMenu(
                        expanded = expandDropdownMenu,
                        onDismissRequest = { expandDropdownMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(
                                text = "Logout",
                                color = MaterialTheme.colorScheme.primary) },
                            onClick = {
                                expandDropdownMenu = false
                                onLogout()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete account",
                                color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                expandDropdownMenu = false
                                onDeleteAccount()}
                        )
                    }
                }
            }
        },
    )
}


@Composable
fun ShowHomeContent(modifier: Modifier = Modifier, navController: NavController, isLoggedIn: Boolean, userData: UserData?) {
    Column(modifier = modifier.padding().fillMaxWidth()) {
        Spacer(modifier = Modifier.padding(3.dp))
        ShowCurrentTime()
        Spacer(modifier = Modifier.padding(3.dp))
        FavoriteSection(navController, isLoggedIn, userData)
    }
}
@Composable
fun ShowCurrentTime() {

    val date = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    val current = date.format(formatter)

    Text(
        text = current.toString(),
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}


@Composable
fun FavoriteSection(navController: NavController, isLoggedIn: Boolean, userData: UserData?) {
    val scrollState = rememberLazyListState()
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            text = "Favorites",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .padding(top = 5.dp, bottom = 5.dp, start = 0.dp, end = 0.dp)
                .fillMaxWidth()

        )
        // If logged in , show the user's favorites:
        // using lazy column

        val uriHandler = LocalUriHandler.current

        val favorites = remember(isLoggedIn, userData) {
            if (isLoggedIn && userData != null) {
                userData.userFavorites
                    .values
                    .sortedByDescending {
                        Instant.parse(it.favoritedTime ?: Instant.now().toString())
                    }
            } else {
                emptyList()
            }
        }

        // show user favorites

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 24.dp),

            verticalArrangement = Arrangement.spacedBy(10.dp),
            state = rememberLazyListState()
        ) {
        if (!isLoggedIn) {

            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                Button(
                    onClick = { navController.navigate("login_screen") },
                ) {
                    Text("Log in to see favorites")
                }
            }
            }

        } else {

            if (favorites.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "No favorites",
                            textAlign = TextAlign.Center,
                        )

                    }
                }
            } else {
                items(favorites) { favorite ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp, start = 5.dp, end = 5.dp)
                            .clickable {
                                navController.navigate("artist_detail_screen/${favorite.artistId}")
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.padding(start = 5.dp)) {
                            Text(favorite.artistName, fontSize = 18.sp)
                            Text(
                                text = "${favorite.artistNationality}, ${favorite.artistBirthday}",
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.fillMaxWidth(0.5f)
                            )
                        }
//                            Text(fav.favoritedTime ?: "", fontSize = 15.sp)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TimeAgo(
                                favorite.favoritedTime ?: Instant.now().toString(),
                                style = TextStyle(fontSize = 15.sp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                modifier = Modifier.padding(end = 0.dp).size(25.dp),
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "detail page"
                            )
                        }
                    }
                }
            }
        }
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(5.dp),
                    contentAlignment = Alignment.Center) {
                    BasicText(
                        buildAnnotatedString {
                            val link =
                                LinkAnnotation.Url(
                                    "https://www.artsy.net/",
                                    TextLinkStyles(
                                        SpanStyle(
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Bold,
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 15.sp
                                        )
                                    )
                                ) {
                                    val url = (it as LinkAnnotation.Url).url
                                    // log some metrics
                                    uriHandler.openUri(url)
                                }
                            withLink(link) { append("Powered by Artsy") }
                        }

                    )
                }
            }

            }


    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistSearchBar(
    isLoggedIn: Boolean,
    state: SearchIconState,
    searchTextState: String,
    onQueryChange: (String) -> Unit,
    onCloseClick: () -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier) {

    val onActiveChange: (Boolean) -> Unit = {}
//    val colors1 = SearchBarDefaults.colors(
//        containerColor = MaterialTheme.colorScheme.primaryContainer,
//        dividerColor = Color.Transparent
//    )
    SearchBar(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        inputField = {

            SearchBarDefaults.InputField(
                query = searchTextState,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                expanded = true,
                onExpandedChange = onActiveChange,
                enabled = true,
                placeholder = {
                    Text(
                        text = "Search artists...",
                        fontSize = 25.sp,
                        modifier = Modifier.padding(vertical = 0.dp)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "search icon"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = {
                        if (searchTextState.isNotEmpty()) {
                            onQueryChange("")
                        } else {
                            onCloseClick()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close icon"
                        )
                    }
                },

                interactionSource = null,
            )
        },
        expanded = true,
        onExpandedChange = onActiveChange,

            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
//                containerColor = Color.Transparent,
                dividerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),

        tonalElevation = 0.dp,
        shadowElevation = 0.dp,

        windowInsets = SearchBarDefaults.windowInsets,
        content = {
            Box(modifier = Modifier.height(0.dp)) {}
        }
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onCloseClick: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(), start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search artists...", fontSize = 20.sp) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            textStyle = TextStyle(fontSize = 20.sp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "search icon"
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
//                        if (query.isNotEmpty()) {
//                            onQueryChange("")
//                        } else {
//                            onCloseClick()
//                        }
                        onCloseClick()
                    }
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "close search bar")
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearch(query)
                keyboardController?.hide()
            }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )

        )
    }
}



//sealed class UserAction {
//    object SearchClicked: UserAction()
//    object CloseClicked: UserAction()
//}
//
//data class SearchState(
//    val isBarVisible: Boolean = false,
//    val query: String = ""
//)

@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SearchArtistBar(
//    text: String,
//    onTextChange: (String) -> Unit,
//    onCloseClicked: () -> Unit,
//    onSearchClicked: (String) -> Unit,
//) {
//    Surface(
//        modifier = Modifier
//            .fillMaxSize()
//            .height(64.dp),
//        color = MaterialTheme.colorScheme.primary
//    ) {
//        TextField(
//            modifier = Modifier.fillMaxWidth(),
//            value = text,
//            onValueChange = {
//                onTextChange(it)
//            },
//            placeholder = {
//                Text(
//                    text = "Search artists...",
//                    fontSize = 25.sp,
//                    modifier = Modifier.padding(vertical = 0.dp)
//                )
//            },
//            textStyle = TextStyle(
//                fontSize = MaterialTheme.typography.titleMedium.fontSize
//            ),
//            singleLine = true,
//            leadingIcon = {
//                IconButton(
//                    onClick = {}
//                ) {
//                    Icon(imageVector = Icons.Default.Search,
//                        contentDescription = "Search Icon")
//                }
//            },
//            trailingIcon = {
//                IconButton(
//                    onClick = {
//                        if (text.isNotEmpty()) {
//                            onTextChange("")
//                        } else {
//                            onCloseClicked()
//                        }
//                    }
//                ) {
//                    Icon(imageVector = Icons.Default.Close,
//                        contentDescription = "Close Icon")
//                }
//            },
//            // keyboard options: IME
//
//            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
//            keyboardActions = KeyboardActions(
//                onSearch = {
//                    onSearchClicked(text)
//                }
//            )
//
//        )
//
//
//    }
//}


@Composable
fun TimeAgo(
    isoTimestamp: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    val timeInMillis = remember(isoTimestamp) {
        Instant.parse(isoTimestamp)
            .toEpochMilli()
    }

    var now by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000L)
            now = System.currentTimeMillis()
        }
    }
    val text = remember(timeInMillis, now) {
        val diff = now - timeInMillis
        when {
            diff <= DateUtils.MINUTE_IN_MILLIS -> {
                val secs = (diff / DateUtils.SECOND_IN_MILLIS).coerceAtLeast(1)
                "$secs second${if (secs == 1L) "" else "s"} ago"
            }
            diff <= DateUtils.HOUR_IN_MILLIS -> {
                val mins = (diff / DateUtils.MINUTE_IN_MILLIS).coerceAtLeast(1)
                "$mins minute${if (mins == 1L) "" else "s"} ago"
            }
            diff <= DateUtils.DAY_IN_MILLIS -> {
                val hrs = (diff / DateUtils.HOUR_IN_MILLIS).coerceAtLeast(1)
                "$hrs hour${if (hrs == 1L) "" else "s"} ago"
            }
            else -> {
                val days = (diff / DateUtils.DAY_IN_MILLIS).coerceAtLeast(1)
                "$days day${if (days == 1L) "" else "s"} ago"
            }
        }
    }

    Text(text, modifier = modifier, style = style)
}
@Composable
@Preview(showBackground = true)
fun SearchAppBarPreview() {
    MyApplicationTheme {
        ArtistSearchBar(
            isLoggedIn = true,
            state = SearchIconState.OPENED,
            searchTextState = "Picasso",
            onQueryChange = {},
            onCloseClick = {},
            onSearch = {}
        )
    }
}
//
//@Composable
//@Preview
//fun SearchBarPreview() {
//        SearchArtistBar(
//            text="Picasso",
//            onTextChange= {},
//            onCloseClicked= {},
//            onSearchClicked={}
//        )
//
//}