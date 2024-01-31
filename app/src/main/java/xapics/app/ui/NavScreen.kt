package xapics.app.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import xapics.app.MainViewModel
import xapics.app.R
import xapics.app.TAG
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Scaffold (
        topBar = {
            TopBar(
                goToHomeScreen = { navController.navigate(NavList.HomeScreen.name) },
                goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) },
                goToAdminScreen = { navController.navigate(NavList.AdminScreen.name) },
                goToProfileScreen = { navController.navigate(NavList.ProfileScreen.name) },
                goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) },
                updateTopBarCaption = viewModel::updateTopBarCaption,
                logOut = viewModel::logOut,
                topBarCaption = appState.topBarCaption,
                page = navBackStackEntry?.destination?.route,
                appState.userId,
            )
        }
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
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) }
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
                    goToAdminScreen = {
                        navController.popBackStack()
                        navController.navigate(NavList.AdminScreen.name)
                    },
                    goToProfileScreen = {
                        navController.popBackStack()
                        navController.navigate(NavList.ProfileScreen.name)
                                        },
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
                    appState.userId,
                    appState.userCollections,
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) },
                    goToEditFilmsScreen = { navController.navigate(NavList.EditFilmsScreen.name) },
                    goToUploadScreen = { navController.navigate(NavList.UploadScreen.name) },
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) }
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopBar(
    goToHomeScreen: () -> Unit,
    goToAuthScreen: () -> Unit,
    goToAdminScreen: () -> Unit,
    goToProfileScreen: () -> Unit,
    goToPicsListScreen: () -> Unit,
    updateTopBarCaption: (String) -> Unit,
    logOut: () -> Unit,
    topBarCaption: String,
    page: String?,
    userId: Int?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            updateTopBarCaption("XA pics")
            goToHomeScreen()
        }) {
            Image(painterResource(R.drawable.xa_pics_mini_closed_export), contentDescription = "Go to home screen", modifier = Modifier.padding(6.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))

        val focusRequester = remember { FocusRequester() }
        val text = when (page) {
//            "HomeScreen" -> "XA Pics"
//            "PicsListScreen" -> topBarCaption
//            "PicScreen" -> topBarCaption
//            "AuthScreen" -> topBarCaption
            "ProfileScreen" -> "Collections"
            else -> topBarCaption
        }
        var searchOn by rememberSaveable { mutableStateOf(false) }
        var queue by rememberSaveable { mutableStateOf("") }
        if(searchOn) {
            TextField(
                value = queue,
                onValueChange = { queue = it },
                keyboardActions = KeyboardActions(onAny = {
                    // TODO search
                    searchOn = false
                }),
                modifier = Modifier
                    .weight(1f)
//                    .height(28.dp)
                    .focusRequester(focusRequester),
                )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        } else {
            Text(
                text = text,
                fontSize = 20.sp,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee()
                    .clickable(enabled = page == "PicScreen") { goToPicsListScreen() }
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        IconButton(onClick = { searchOn = !searchOn }) {
            Icon(Icons.Default.Search, "Search photos")
        }
        Spacer(modifier = Modifier.width(6.dp))
        if(page == "AdminScreen" || page == "ProfileScreen") {
            IconButton(onClick = {
                logOut() // TODO when logOut() finished -> goToAuthScreen()
                goToAuthScreen()
            }) {
                Icon(painterResource(id = R.drawable.baseline_logout_24), "Log out")
            }
        } else {
            IconButton(
                enabled = page != "AuthScreen",
                onClick = {
                    when (userId) {
                        null -> goToAuthScreen()
                        1 -> goToAdminScreen()
                        else -> goToProfileScreen()
                    }
                }
            ) {
                Icon(Icons.Outlined.AccountCircle, "Go to Profile screen")
            }
        }
    }
}