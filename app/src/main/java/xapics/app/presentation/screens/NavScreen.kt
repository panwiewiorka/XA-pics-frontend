package xapics.app.presentation.screens

import android.annotation.SuppressLint
import android.os.Build
import android.view.WindowInsetsController
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import xapics.app.R
import xapics.app.presentation.MainViewModel
import xapics.app.presentation.components.TopBar
import xapics.app.presentation.screens.homeScreen.HomeScreen
import xapics.app.presentation.screens.picScreen.PicScreen
import xapics.app.presentation.screens.profileScreen.ProfileScreen
import xapics.app.presentation.topBar.TopBarViewModel

enum class NavList(@StringRes val title: Int) {
    HomeScreen(title = R.string.home_screen),
    PicsListScreen(title = R.string.pics_list_screen),
    PicScreen(title = R.string.pic_screen),
    SearchScreen(title = R.string.search_screen),
    AuthScreen(title = R.string.auth_screen),
    ProfileScreen(title = R.string.profile_screen),
}

val Int.nonScaledSp
    @Composable
    get() = (this / LocalDensity.current.fontScale).sp

@SuppressLint("RestrictedApi")
@Composable
fun NavScreen(
    navController: NavHostController = rememberNavController(),
) {

    val viewModel: MainViewModel = hiltViewModel()
    val appState by viewModel.appState.collectAsState()
    val state by viewModel.state.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = NavList.valueOf(
        backStackEntry?.destination?.route ?: NavList.HomeScreen.name
    )

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
        val controller = LocalView.current.windowInsetsController

        LaunchedEffect(appState.isFullscreen) {
            if (appState.isFullscreen) {
                controller?.apply {
                    hide(WindowInsetsCompat.Type.statusBars())
                    hide(WindowInsetsCompat.Type.navigationBars())
                    systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                controller?.apply {
                    show(WindowInsetsCompat.Type.statusBars())
                    show(WindowInsetsCompat.Type.navigationBars())
                    systemBarsBehavior = WindowInsetsController.BEHAVIOR_DEFAULT
                }
            }
        }
    }

    Scaffold (
        modifier = Modifier.pointerInput(appState.showSearch) {
            detectTapGestures(onTap = {
                if (appState.showSearch) viewModel.showSearch(false)
            })
        },
        topBar = {
            val topBarViewModel: TopBarViewModel = hiltViewModel()
            val topBarState by topBarViewModel.state.collectAsState()
            if (!appState.isFullscreen) {
                TopBar(
                    search = viewModel::search,
                    showSearch = viewModel::showSearch,
                    loadStateSnapshot = viewModel::loadStateSnapshot,
                    showPicsList = viewModel::showPicsList,
                    logOut = topBarViewModel::logOut,
                    appState = appState,
                    state = topBarState,
                    goBack = { navController.navigateUp() },
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) {
                        popUpTo(NavList.HomeScreen.name)
                    } },
                    goToProfileScreen = { navController.navigate(NavList.ProfileScreen.name) },
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) },
                    goToSearchScreen = { navController.navigate(NavList.SearchScreen.name) },
                    page = backStackEntry?.destination?.route,
                    previousPage = navController.previousBackStackEntry?.destination?.route,
                    pageName = currentScreen.title,
                )
            }
        },
    ) { innerPadding ->
        BackHandler(
            enabled = backStackEntry?.destination?.route == NavList.PicsListScreen.name
                    || backStackEntry?.destination?.route == NavList.PicScreen.name
        ) {
            viewModel.loadStateSnapshot()

            when (backStackEntry?.destination?.route) {
                NavList.PicsListScreen.name -> viewModel.showPicsList(false)
                NavList.PicScreen.name -> viewModel.changeFullScreenMode(false)
            }
            navController.navigateUp()
        }

        NavHost(
            navController = navController,
            startDestination = NavList.HomeScreen.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = NavList.HomeScreen.name) {
                HomeScreen(
                    authenticate = viewModel::authenticate,
                    getRollThumbs = viewModel::getRollThumbs,
                    getAllTags = viewModel::getAllTags,
                    showConnectionError = viewModel::showConnectionError,
                    getRandomPic = viewModel::getRandomPic,
                    search = viewModel::search,
                    appState = appState,
                    state = state,
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) },
                    updateAndGoToPicScreen = {
//                        viewModel.updatePicsList(listOf(appState.pic!!)) // todo check
                        viewModel.saveStateSnapshot(replaceExisting = false, picsList = listOf(state.pic!!), picIndex = 0, topBarCaption = "Random pic")
//                        viewModel.saveNewStateSnapshot("Random pic")
                        navController.navigate(NavList.PicScreen.name)
                                    },
                    goToSearchScreen = { navController.navigate(NavList.SearchScreen.name) },
                )
            }
            composable(route = NavList.PicsListScreen.name) {
                PicsListScreen(
                    showPicsList = viewModel::showPicsList,
                    search = viewModel::search,
                    getCollection = viewModel::getCollection,
                    showConnectionError = viewModel::showConnectionError,
                    updatePicState = viewModel::updatePicState,
                    saveStateSnapshot = viewModel::saveStateSnapshot,
                    toDo = viewModel.onPicsListScreenRefresh,
                    appState = appState,
                    state = state,
                    goToPicScreen = { navController.navigate(NavList.PicScreen.name) },
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) },
                    goBack = { navController.navigateUp() },
                    previousPage = navController.previousBackStackEntry?.destination?.route,
                )
            }
            composable(route = NavList.PicScreen.name) {
                PicScreen(
                    search = viewModel::search,
                    saveStateSnapshot = viewModel::saveStateSnapshot,
                    getCollection = viewModel::getCollection,
                    editCollection = viewModel::editCollection,
                    updateCollectionToSaveTo = viewModel::updateCollectionToSaveTo,
                    changeFullScreenMode = viewModel::changeFullScreenMode,
                    updatePicState = viewModel::updatePicState,
//                    updateStateSnapshot = viewModel::saveStateSnapshot,
                    showConnectionError = viewModel::showConnectionError,
                    appState = appState,
                    state = state,
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) }
                ) { navController.navigate(NavList.AuthScreen.name) }
            }
            composable(route = NavList.SearchScreen.name) {
                SearchScreen(
                    search = viewModel::search,
                    getAllTags = viewModel::getAllTags,
                    getFilteredTags = viewModel::getFilteredTags,
                    appState = appState,
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) },
                )
            }
            composable(route = NavList.AuthScreen.name) {
                AuthScreen(
                    updateTopBarCaption = {}, // TODO
                    updateUserName = viewModel::updateUserName,
                    rememberToGetBackAfterLoggingIn = viewModel::rememberToGetBackAfterLoggingIn,
                    signUpOrIn = viewModel::signUpOrIn,
                    authResults = viewModel.authResults,
                    getBackAfterLoggingIn = appState.getBackAfterLoggingIn,
                    goBack = { navController.navigateUp() },
                    goToProfileScreen = { navController.navigate(NavList.ProfileScreen.name) },
                    isLoading = appState.isLoading,
                )
            }
            composable(route = NavList.ProfileScreen.name) {
                ProfileScreen(
                    updateUserCollections = viewModel::updateUserCollections,
                    getUserInfo = viewModel::getUserInfo,
                    showConnectionError = viewModel::showConnectionError,
                    getCollection = viewModel::getCollection,
                    renameOrDeleteCollection = viewModel::renameOrDeleteCollection,
                    appState = appState,
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) },
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) }
                )
            }
        }
    }
}