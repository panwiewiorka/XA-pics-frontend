package xapics.app.presentation.screens

import android.annotation.SuppressLint
import android.os.Build
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
import xapics.app.presentation.SharedViewModel
import xapics.app.presentation.components.topBar.TopBar
import xapics.app.presentation.components.topBar.TopBarViewModel
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


@SuppressLint("RestrictedApi")
@Composable
fun NavScreen(
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route?.substringAfterLast('.')?.substringBefore('/') ?: "XA pics"
    val prevScreen = navController.previousBackStackEntry?.destination?.route?.substringAfterLast('.')?.substringBefore('/') ?: "XA pics"

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
                    loadCaption = topBarViewModel::loadCaption,
                    populateCaptionTable = topBarViewModel::populateCaptionTable,
                    caption = captionState,
                    logOut = topBarViewModel::logOut,
                    tags = stateSnapshot.tags,
                    goBack = { navController.navigateUp() },
                    onProfileClick = topBarViewModel::onProfileClick,
                    goToAuthScreen = { navController.navigate(Screen.Auth(false)) {
                        popUpTo(Screen.Home)
                    } },
                    goToProfileScreen = { navController.navigate(Screen.Profile) },
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
        NavHost(
            navController = navController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Home> {
                val homeViewModel: HomeViewModel = hiltViewModel()
                val homeState by homeViewModel.homeState.collectAsState()
                HomeScreen(
                    getRollThumbs = homeViewModel::getRollThumbs,
                    getAllTags = homeViewModel::getAllTags,
                    showConnectionError = homeViewModel::showConnectionError,
                    getRandomPic = homeViewModel::getRandomPic,
                    homeState = homeState,
                    goToPicsListScreen = { searchQuery -> navController.navigate(Screen.PicsList(searchQuery)) },
                    updateAndGoToPicScreen = {
                        homeViewModel.updatePicsListToRandomPic(
                            pic = homeState.randomPic!!,
                            goToPicScreen = { navController.navigate(Screen.Pic(-1)) }
                        )
                        homeViewModel.saveCaption(replaceExisting = false, topBarCaption = "Random pic")
                    }
                ) { navController.navigate(Screen.Search) }
            }
            composable<Screen.PicsList> {
                val picsListViewModel: PicsListViewModel = hiltViewModel()
                PicsListScreen(
                    authResults = picsListViewModel.authResults,
                    messages = picsListViewModel.messages,
                    isLoading = picsListViewModel.isLoading,
                    search = picsListViewModel::search,
                    query = picsListViewModel.query,
                    getCollection = picsListViewModel::getCollection,
                    connectionErrorIsShown = picsListViewModel.connectionError,
                    showConnectionError = picsListViewModel::showConnectionError,
                    saveCaption = picsListViewModel::saveCaption,
                    picsList = picsListViewModel.picsList,
                    goToPicScreen = { picIndex -> navController.navigate(Screen.Pic(picIndex)) },
                    goToAuthScreen = { navController.navigate(Screen.Auth(true)) },
                    goBack = { navController.navigateUp() },
                    previousPage = prevScreen,
                )
            }
            composable<Screen.Pic> {
                val picViewModel: PicViewModel = hiltViewModel()
                val picScreenState by picViewModel.picScreenState.collectAsState()
                PicScreen(
                    messages = picViewModel.messages,
                    editCollection = picViewModel::editCollection,
                    updateCollectionToSaveTo = picViewModel::updateCollectionToSaveTo,
                    updatePicInfo = picViewModel::updatePicInfo,
                    changeFullScreenMode = sharedViewModel::changeFullscreenMode,
                    isFullscreen = sharedViewModel.isFullscreen,
                    picScreenState = picScreenState,
                    goToPicsListScreen = { searchQuery -> navController.navigate(Screen.PicsList(searchQuery)) }
                ) { navController.navigate(Screen.Auth(true)) }
            }
            composable<Screen.Search> {
                val searchViewModel: SearchViewModel = hiltViewModel()
                SearchScreen(
                    getAllTags = searchViewModel::getAllTags,
                    getFilteredTags = searchViewModel::getFilteredTags,
                    tags = searchViewModel.tags,
                    messages = searchViewModel.messages,
                    isLoading = searchViewModel.isLoading,
                    connectionError = searchViewModel.connectionError,
                    showConnectionError = searchViewModel::showConnectionError,
                ) { searchQuery -> navController.navigate(Screen.PicsList(searchQuery)) }
            }
            composable<Screen.Auth> {
                val authViewModel: AuthViewModel = hiltViewModel()
                AuthScreen(
                    saveCaption = authViewModel::saveCaption,
                    loadCaption = authViewModel::loadCaption,
                    signUpOrIn = authViewModel::signUpOrIn,
                    authResults = authViewModel.authResults,
                    goBackAfterLogIn = authViewModel.goBackAfterLogIn,
                    goBack = { navController.navigateUp() },
                    goToProfileScreen = { navController.navigate(Screen.Profile) },
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
                    renameOrDeleteCollection = profileViewModel::renameOrDeleteCollection,
                    profileState = profileState,
                    goToAuthScreen = { navController.navigate(Screen.Auth(true)) },
                    goToPicsListScreen = { searchQuery -> navController.navigate(Screen.PicsList(searchQuery)) }
                )
            }
        }

        BackHandler(
            enabled = currentScreen != Screen.Home.NAME
        ) {
            sharedViewModel.loadCaption()

            if (currentScreen == Screen.Pic.NAME) sharedViewModel.changeFullscreenMode(false)

            navController.navigateUp()
        }
    }
}