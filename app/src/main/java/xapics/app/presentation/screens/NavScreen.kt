package xapics.app.presentation.screens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.WindowInsetsController
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import xapics.app.Screen
import xapics.app.TAG
import xapics.app.presentation.SharedViewModel
import xapics.app.presentation.screens.auth.AuthScreen
import xapics.app.presentation.screens.auth.AuthViewModel
import xapics.app.presentation.screens.home.HomeScreen
import xapics.app.presentation.screens.home.HomeViewModel
import xapics.app.presentation.screens.pic.PicScreen
import xapics.app.presentation.screens.pic.PicViewModel
import xapics.app.presentation.screens.picsList.PicsListScreen
import xapics.app.presentation.screens.picsList.PicsListViewModel
import xapics.app.presentation.screens.profile.ProfileScreen
import xapics.app.presentation.screens.profile.ProfileViewModel
import xapics.app.presentation.screens.search.SearchScreen
import xapics.app.presentation.screens.search.SearchViewModel
import xapics.app.presentation.topBar.TopBar
import xapics.app.presentation.topBar.TopBarViewModel


@SuppressLint("RestrictedApi")
@Composable
fun NavScreen(
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route?.substringAfterLast('.')?.substringBeforeLast('/') ?: "XA pics"
    val prevScreen = navController.previousBackStackEntry?.destination?.route?.substringAfterLast('.')?.substringBeforeLast('/') ?: "XA pics"

    val sharedViewModel: SharedViewModel = hiltViewModel()

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
        val controller = LocalView.current.windowInsetsController

        LaunchedEffect(sharedViewModel.isFullscreen) {
            if (sharedViewModel.isFullscreen) {
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
        modifier = Modifier.pointerInput(sharedViewModel.searchIsShown) {
            detectTapGestures(onTap = {
                if (sharedViewModel.searchIsShown) sharedViewModel.showSearch(false)
            })
        },
        topBar = {
            val topBarViewModel: TopBarViewModel = hiltViewModel()
            val captionState by topBarViewModel.captionState.collectAsState()
            val stateSnapshot by topBarViewModel.stateSnapshot.collectAsState()
            if (!sharedViewModel.isFullscreen) {
                TopBar(
                    searchIsShown = sharedViewModel.searchIsShown,
                    showSearch = sharedViewModel::showSearch,
                    loadStateSnapshot = topBarViewModel::loadCaption,
                    logOut = topBarViewModel::logOut,
                    tags = stateSnapshot.tags,
                    caption = captionState,
                    goBack = { navController.navigateUp() },
                    onProfileClick = topBarViewModel::onProfileClick,
                    goToAuthScreen = { isAuthorized -> navController.navigate(Screen.Auth(false, isAuthorized)) {
                        popUpTo(Screen.Home)
                    } },
                    goToProfileScreen = { userName ->  navController.navigate(Screen.Profile(userName)) },
                    goToPicsListScreen = { searchQuery -> navController.navigate(Screen.PicsList(searchQuery)) },
                    goToSearchScreen = {
                        topBarViewModel.onGoToSearchScreen()
                        navController.navigate(Screen.Search)
                                       },
                    page = currentScreen,
                    previousPage = prevScreen,
                )
            }
        },
    ) { innerPadding ->
        BackHandler(
            enabled = true
//            enabled = currentScreen == Screen.PicsList.NAME
//                    || currentScreen == Screen.Pic.NAME
        ) {
            Log.d(TAG, "NavScreen: BackHandler")
            sharedViewModel.loadCaption()

            when (currentScreen) {
                Screen.Pic.NAME -> sharedViewModel.changeFullscreenMode(false)
            }
            navController.navigateUp()
        }

        NavHost(
            navController = navController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Home> {
                val homeViewModel: HomeViewModel = hiltViewModel()
                val homeState by homeViewModel.homeState.collectAsState()
                HomeScreen(
                    authenticate = homeViewModel::authenticate,
                    getRollThumbs = homeViewModel::getRollThumbs,
                    getAllTags = homeViewModel::getAllTags,
                    showConnectionError = homeViewModel::showConnectionError,
                    getRandomPic = homeViewModel::getRandomPic,
                    homeState = homeState,
                    goToPicsListScreen = { searchQuery -> navController.navigate(Screen.PicsList(searchQuery)) },
                    updateAndGoToPicScreen = {
                        homeViewModel.saveCaption(replaceExisting = false, topBarCaption = "Random pic")
                        navController.navigate(Screen.Pic(0))
                                    },
                    goToSearchScreen = { navController.navigate(Screen.Search) }
                )
            }
            composable<Screen.PicsList> {
                val picsListViewModel: PicsListViewModel = hiltViewModel()
                PicsListScreen(
                    isLoading = picsListViewModel.isLoading,
                    search = picsListViewModel::search,
                    query = picsListViewModel.query,
                    getCollection = picsListViewModel::getCollection, // todo
                    connectionErrorIsShown = picsListViewModel.connectionError,
                    showConnectionError = picsListViewModel::showConnectionError,
                    saveCaption = picsListViewModel::saveCaption,
                    picsList = picsListViewModel.picsList,
                    goToPicScreen = { picIndex -> navController.navigate(Screen.Pic(picIndex)) },
                    goToAuthScreen = { navController.navigate(Screen.Auth(true, false)) },
                    goBack = { navController.navigateUp() },
                    previousPage = prevScreen,
                )
            }
            composable<Screen.Pic> {
                val picViewModel: PicViewModel = hiltViewModel()
                val picScreenState by picViewModel.picScreenState.collectAsState()
                PicScreen(
                    getCollection = picViewModel::getCollection,
                    editCollection = picViewModel::editCollection,
                    updateCollectionToSaveTo = picViewModel::updateCollectionToSaveTo,
                    updatePicInfo = picViewModel::updatePicInfo,
                    changeFullScreenMode = sharedViewModel::changeFullscreenMode,
                    isFullscreen = sharedViewModel.isFullscreen,
                    showConnectionError = picViewModel::showConnectionError,
                    picScreenState = picScreenState,
                    goToPicsListScreen = { searchQuery -> navController.navigate(Screen.PicsList(searchQuery)) }
                ) { navController.navigate(Screen.Auth(true, false)) }
            }
            composable<Screen.Search> {
                val searchViewModel: SearchViewModel = hiltViewModel()
                SearchScreen(
                    getAllTags = searchViewModel::getAllTags,
                    getFilteredTags = searchViewModel::getFilteredTags,
                    tags = searchViewModel.tags,
                ) { searchQuery -> navController.navigate(Screen.PicsList(searchQuery)) }
            }
            composable<Screen.Auth> {
                val authViewModel: AuthViewModel = hiltViewModel()
                AuthScreen(
                    saveCaption = authViewModel::saveCaption,
                    signUpOrIn = authViewModel::signUpOrIn,
                    authResults = authViewModel.authResults,
                    goBackAfterLogIn = authViewModel.goBackAfterLogIn,
                    goBack = { navController.navigateUp() },
                    goToProfileScreen = { userName ->  navController.navigate(Screen.Profile(userName)) },
                    isLoading = authViewModel.isLoading,
                )
            }
            composable<Screen.Profile> {
                val profileViewModel: ProfileViewModel = hiltViewModel()
                val profileState by profileViewModel.profileState.collectAsState()
                ProfileScreen(
                    updateUserCollections = profileViewModel::updateUserCollections,
                    getUserInfo = profileViewModel::getUserInfo,
                    showConnectionError = profileViewModel::showConnectionError,
                    getCollection = profileViewModel::getCollection,
                    renameOrDeleteCollection = profileViewModel::renameOrDeleteCollection,
                    profileState = profileState,
                    goToAuthScreen = { navController.navigate(Screen.Auth(true, false)) },
                    goToPicsListScreen = { searchQuery -> navController.navigate(Screen.PicsList(searchQuery)) }
                )
            }
        }
    }
}