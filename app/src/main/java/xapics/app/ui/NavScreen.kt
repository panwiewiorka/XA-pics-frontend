package xapics.app.ui

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import xapics.app.MainViewModel
import xapics.app.R
import xapics.app.ui.auth.AuthScreen
import xapics.app.ui.auth.ProfileScreen

enum class NavList(@StringRes val title: Int) {
    HomeScreen(title = R.string.home_screen),
    PicsListScreen(title = R.string.pics_list_screen),
    PicScreen(title = R.string.pic_screen),
    EditFilmsScreen(title = R.string.edit_films_screen),
    UploadScreen(title = R.string.upload_screen),
    AuthScreen(title = R.string.auth_screen),
    AdminScreen(title = R.string.admin_screen),
    ProfileScreen(title = R.string.profile_screen),
}

@SuppressLint("RestrictedApi")
@Composable
fun NavScreen(
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState
) {

    val viewModel: MainViewModel = hiltViewModel()
    val appState by viewModel.appState.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = NavList.valueOf(
        backStackEntry?.destination?.route ?: NavList.HomeScreen.name
    )

    Scaffold (
        modifier = Modifier.pointerInput(appState.showSearch) {
            detectTapGestures(onTap = {
                if (appState.showSearch) viewModel.changeShowSearchState()
            })
        },
        topBar = {
            TopBar(
                popBackStack = { navController.popBackStack() },
                goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) },
                goToAdminScreen = { navController.navigate(NavList.AdminScreen.name) },
                goToProfileScreen = { navController.navigate(NavList.ProfileScreen.name) },
                goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) },
                updateTopBarCaption = viewModel::updateTopBarCaption,
                search = viewModel::search,
                showSearch = appState.showSearch,
                changeShowSearchState = viewModel::changeShowSearchState,
                logOut = viewModel::logOut,
                topBarCaption = appState.topBarCaption,
                page = backStackEntry?.destination?.route,
                previousPage = navController.previousBackStackEntry?.destination?.route,
                pageName = currentScreen.title,
                userName = appState.userName,
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavList.HomeScreen.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = NavList.HomeScreen.name) {
                HomeScreen(
                    viewModel,
                    appState,
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) }
                )
            }
            composable(route = NavList.PicsListScreen.name) {
                PicsListScreen(
                    viewModel,
                    appState,
                    goToPicScreen = { navController.navigate(NavList.PicScreen.name) }
                )
            }
            composable(route = NavList.PicScreen.name) {
                PicScreen(
                    viewModel,
                    appState,
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) },
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) }
                )
            }
            composable(route = NavList.EditFilmsScreen.name) {
                EditFilmsScreen(
                    viewModel,
                    appState,
                    goToHomeScreen = { navController.navigate(NavList.HomeScreen.name) },
                    goToUploadScreen = { navController.navigate(NavList.UploadScreen.name) },
                    snackbarHostState
                )
            }
            composable(route = NavList.UploadScreen.name) {
                UploadScreen(
                    viewModel,
                    appState,
                    goToHomeScreen = { navController.navigate(NavList.HomeScreen.name) },
                    goToEditFilmsScreen = { navController.navigate(NavList.EditFilmsScreen.name) },
                    snackbarHostState
                )
            }
            composable(route = NavList.AuthScreen.name) {
                AuthScreen(
                    viewModel,
                    popBackStack = { navController.popBackStack() },
                    goToAdminScreen = { navController.navigate(NavList.AdminScreen.name) },
                    goToProfileScreen = { navController.navigate(NavList.ProfileScreen.name) },
                )
            }
            composable(route = NavList.AdminScreen.name) {
                AdminScreen(
                    viewModel,
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) },
                    goToEditFilmsScreen = { navController.navigate(NavList.EditFilmsScreen.name) },
                    goToUploadScreen = { navController.navigate(NavList.UploadScreen.name) },
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) }
                )
            }
            composable(route = NavList.ProfileScreen.name) {
                ProfileScreen(
                    viewModel,
                    appState.isLoading,
                    appState.userName,
                    appState.userCollections,
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) }
                ) { navController.navigate(NavList.PicsListScreen.name) }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    popBackStack: () -> Unit,
    goToAuthScreen: () -> Unit,
    goToAdminScreen: () -> Unit,
    goToProfileScreen: () -> Unit,
    goToPicsListScreen: () -> Unit,
    updateTopBarCaption: (String) -> Unit,
    search: (String) -> Unit,
    showSearch: Boolean,
    changeShowSearchState: () -> Unit,
    logOut: () -> Unit,
    topBarCaption: String,
    page: String?,
    previousPage: String?,
    @StringRes pageName: Int,
    userName: String?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val focusRequester = remember { FocusRequester() }
        val text = when (page) {
            "PicsListScreen" -> topBarCaption
            "PicScreen" -> topBarCaption
            "ProfileScreen" -> userName ?: ""
            else -> stringResource(id = pageName)
        }
        var showClearSearchButton by rememberSaveable { mutableStateOf(false) }
        val theText = ""
        var query by remember { mutableStateOf( TextFieldValue (
            text = theText,
            selection = TextRange(theText.length)
        )) }

        if (page == "ProfileScreen" && page == previousPage) popBackStack()

        if (page == "HomeScreen") {
            IconButton(enabled = false, onClick = {  }) {
                Image(painterResource(R.drawable.xa_pics_mini_closed_export), contentDescription = null, modifier = Modifier.padding(6.dp))
            }
        } else {
            IconButton(onClick = { popBackStack() }) {
                Icon(Icons.Outlined.ArrowBack, "go Back")
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        if (showSearch) {
            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = query,
                    onValueChange = {
                        showClearSearchButton = false
                        query = it
                                    },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.LightGray),
                    cursorBrush = SolidColor(Color.LightGray),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (query.text != "") {
                            search(query.text)
                            goToPicsListScreen()
                        }
                        changeShowSearchState()
//                        showSearch = false
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
//                        .weight(1f)
//                    .height(28.dp)
//                    .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(20.dp))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .focusRequester(focusRequester),
                )
                if (showClearSearchButton && query.text.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "clear search",
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .align(Alignment.CenterEnd)
                            .clickable {
                                query = TextFieldValue("")
                                showClearSearchButton = false
                            }
                    )
                }
            }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                showClearSearchButton = true
            }
        } else {
            Text(
                text = text,
                fontSize = 20.sp,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
                    .weight(1f)
                    .clickable(enabled = page == "PicScreen") { goToPicsListScreen() }
            )
        }
        IconButton(onClick = {
            if (showSearch && query.text != "") {
                search(query.text)
                goToPicsListScreen()
            }
            changeShowSearchState()
        }) {
            Icon(Icons.Default.Search, "Search photos")
        }

        Spacer(modifier = Modifier.width(6.dp))

        if(page == "AdminScreen" || page == "ProfileScreen") {
            IconButton(onClick = {
                popBackStack()
                logOut() // TODO when logOut() finished -> goToAuthScreen()
                goToAuthScreen()
            }) {
                Icon(painterResource(id = R.drawable.baseline_logout_24), "Log out")
            }
        } else {
            IconButton(
                enabled = page != "AuthScreen",
                onClick = {
                    when (userName) {
                        null -> goToAuthScreen()
                        "admin" -> goToAdminScreen()
                        else -> goToProfileScreen()
                    }
                }
            ) {
                Icon(Icons.Outlined.AccountCircle, "Go to Profile screen")
            }
        }
    }
    LaunchedEffect(page) {
        if(showSearch) changeShowSearchState()
    }
}