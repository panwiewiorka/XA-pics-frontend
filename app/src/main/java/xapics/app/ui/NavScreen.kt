package xapics.app.ui

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
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
import xapics.app.ui.composables.TopBar

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
                    viewModel = viewModel,
                    appState = appState,
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) }
                )
            }
            composable(route = NavList.PicsListScreen.name) {
                PicsListScreen(
                    viewModel = viewModel,
                    appState = appState,
                    goToPicScreen = { navController.navigate(NavList.PicScreen.name) },
                    caption = appState.picsListQuery?.flattenToString() ?: ""
                )
            }
            composable(route = NavList.PicScreen.name) {
                PicScreen(
                    viewModel = viewModel,
                    appState = appState,
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) },
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) }
                )
            }
            composable(route = NavList.EditFilmsScreen.name) {
                EditFilmsScreen(
                    viewModel = viewModel,
                    appState = appState,
                    goToHomeScreen = { navController.navigate(NavList.HomeScreen.name) },
                    goToUploadScreen = { navController.navigate(NavList.UploadScreen.name) },
                    snackbarHostState = snackbarHostState
                )
            }
            composable(route = NavList.UploadScreen.name) {
                UploadScreen(
                    viewModel = viewModel,
                    appState = appState,
                    goToHomeScreen = { navController.navigate(NavList.HomeScreen.name) },
                    goToEditFilmsScreen = { navController.navigate(NavList.EditFilmsScreen.name) },
                    snackbarHostState = snackbarHostState
                )
            }
            composable(route = NavList.AuthScreen.name) {
                AuthScreen(
                    viewModel = viewModel,
                    popBackStack = { navController.popBackStack() },
                    goToAdminScreen = { navController.navigate(NavList.AdminScreen.name) },
                    goToProfileScreen = { navController.navigate(NavList.ProfileScreen.name) },
                )
            }
            composable(route = NavList.AdminScreen.name) {
                AdminScreen(
                    viewModel = viewModel,
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) },
                    goToEditFilmsScreen = { navController.navigate(NavList.EditFilmsScreen.name) },
                    goToUploadScreen = { navController.navigate(NavList.UploadScreen.name) },
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) }
                )
            }
            composable(route = NavList.ProfileScreen.name) {
                ProfileScreen(
                    viewModel = viewModel,
                    isLoading = appState.isLoading,
                    userName = appState.userName,
                    userCollections = appState.userCollections,
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) },
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) }
                )
            }
        }
    }
}