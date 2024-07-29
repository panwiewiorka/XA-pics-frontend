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
import xapics.app.presentation.screens.profileScreen.ProfileScreen
import xapics.app.presentation.screens.homeScreen.HomeScreen
import xapics.app.presentation.screens.picScreen.composables.PicDetails
import xapics.app.presentation.screens.picScreen.PicScreen
import xapics.app.presentation.composables.TopBar
import xapics.app.presentation.windowInfo

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
            if (!appState.isFullscreen) {
                TopBar(
                    search = viewModel::search,
                    showSearch = viewModel::showSearch,
                    loadStateSnapshot = viewModel::loadStateSnapshot,
                    showPicsList = viewModel::showPicsList,
                    logOut = viewModel::logOut,
                    appState = appState,
                    goBack = { navController.navigateUp() },
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) { popUpTo(
                        NavList.HomeScreen.name) } },
                    goToProfileScreen = { navController.navigate(NavList.ProfileScreen.name) },
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) },
                    goToSearchScreen = { navController.navigate(NavList.SearchScreen.name) },
                    page = backStackEntry?.destination?.route,
                    previousPage = navController.previousBackStackEntry?.destination?.route,
                    pageName = currentScreen.title,
                )
            }
        },
        bottomBar = {
            if (!windowInfo().isPortraitOrientation && !appState.isFullscreen && backStackEntry?.destination?.route == "PicScreen") {
                PicDetails(
                    search = viewModel::search,
                    saveStateSnapshot = viewModel::saveStateSnapshot,
                    getCollection = viewModel::getCollection,
                    editCollection = viewModel::editCollection,
                    updateCollectionToSaveTo = viewModel::updateCollectionToSaveTo,
                    changeBlurContent = viewModel::changeBlurContent,
                    appState = appState,
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) },
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) }
                )
            }
        },
    ) { innerPadding ->
        BackHandler(
            enabled = backStackEntry?.destination?.route == NavList.PicsListScreen.name
                    || backStackEntry?.destination?.route == NavList.PicScreen.name
        ) {
            when (backStackEntry?.destination?.route) {
                NavList.PicsListScreen.name -> {
                    viewModel.loadStateSnapshot()
                    viewModel.showPicsList(false)
                }
                NavList.PicScreen.name -> {
                    viewModel.loadStateSnapshot()
                    viewModel.changeFullScreenMode(false)
                }
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
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) },
                    updateAndGoToPicScreen = {
                        viewModel.updatePicsList(listOf(appState.pic!!))
                        viewModel.updateTopBarCaption("Random pic")
                        viewModel.saveStateSnapshot()
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
                    changeBlurContent = viewModel::changeBlurContent,
                    changeFullScreenMode = viewModel::changeFullScreenMode,
                    updateTopBarCaption = viewModel::updateTopBarCaption,
                    updatePicState = viewModel::updatePicState,
                    updateStateSnapshot = viewModel::updateStateSnapshot,
                    showConnectionError = viewModel::showConnectionError,
                    updatePicDetailsWidth = viewModel::updatePicDetailsWidth,
                    stateHistory = viewModel.stateHistory,
                    appState = appState,
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) },
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) }
                )
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
                    updateTopBarCaption = viewModel::updateTopBarCaption,
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