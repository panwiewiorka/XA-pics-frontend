package xapics.app.presentation.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import xapics.app.Screen
import xapics.app.TAG
import xapics.app.presentation.MainViewModel
import xapics.app.presentation.screens.auth.AuthScreen
import xapics.app.presentation.screens.auth.AuthViewModel
import xapics.app.presentation.screens.home.HomeScreen
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
    val viewModel: MainViewModel = hiltViewModel()
    val appState by viewModel.appState.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route?.substringAfterLast('.')?.substringBeforeLast('/') ?: "XA pics"
    val prevScreen = navController.previousBackStackEntry?.destination?.route?.substringAfterLast('.')?.substringBeforeLast('/') ?: "XA pics"

    Scaffold (
        modifier = Modifier.pointerInput(appState.showSearch) {
            detectTapGestures(onTap = {
                if (appState.showSearch) viewModel.showSearch(false)
            })
        },
        topBar = {
            val topBarViewModel: TopBarViewModel = hiltViewModel()
            val captionState by topBarViewModel.captionState.collectAsState()
            val stateSnapshot by topBarViewModel.stateSnapshot.collectAsState()
            if (!appState.isFullscreen) {
                TopBar(
                    searchIsShown = appState.showSearch,
                    showSearch = viewModel::showSearch,
                    loadStateSnapshot = topBarViewModel::loadCaption,
                    logOut = topBarViewModel::logOut,
//                    appState = appState, // todo
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
            Log.d(TAG, "NavScreen: GGGGG")
            viewModel.loadCaption()

            when (currentScreen) {
                Screen.Pic.NAME -> viewModel.changeFullScreenMode(false)
            }
            navController.navigateUp()
        }

        NavHost(
            navController = navController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Home> {
                HomeScreen(
                    authenticate = viewModel::authenticate,
                    getRollThumbs = viewModel::getRollThumbs,
                    getAllTags = viewModel::getAllTags,
                    showConnectionError = viewModel::showConnectionError,
                    getRandomPic = viewModel::getRandomPic,
                    appState = appState,
                    goToPicsListScreen = { searchQuery -> navController.navigate(Screen.PicsList(searchQuery)) },
                    updateAndGoToPicScreen = {
                        viewModel.saveCaption(replaceExisting = false, topBarCaption = "Random pic")
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
                    getCollection = viewModel::getCollection, // todo
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
                    changeFullScreenMode = picViewModel::changeFullScreenMode,
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
                    updateUserName = viewModel::updateUserName, // todo pass username to ProfileScreen or save in DB, + remove from HomeViewModel
//                    rememberToGetBackAfterLoggingIn = authViewModel::rememberToGetBackAfterLoggingIn,
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