package xapics.app.presentation.screens

import android.annotation.SuppressLint
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
import xapics.app.NavList
import xapics.app.presentation.MainViewModel
import xapics.app.presentation.screens.home.HomeScreen
import xapics.app.presentation.screens.pic.PicScreen
import xapics.app.presentation.screens.pic.PicViewModel
import xapics.app.presentation.screens.picsList.PicsListScreen
import xapics.app.presentation.screens.picsList.PicsListViewModel
import xapics.app.presentation.screens.profile.ProfileScreen
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
    val state by viewModel.state.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = NavList.valueOf(
        backStackEntry?.destination?.route ?: NavList.HomeScreen.name
    )

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
//                NavList.PicsListScreen.name -> viewModel.showPicsList(false)
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
                        viewModel.saveStateSnapshot(replaceExisting = false, picsList = listOf(state.pic!!), picIndex = 0, topBarCaption = "Random pic")
                        navController.navigate(NavList.PicScreen.name)
                                    },
                    goToSearchScreen = { navController.navigate(NavList.SearchScreen.name) },
                )
            }
            composable(route = NavList.PicsListScreen.name) {
                val picsListViewModel: PicsListViewModel = hiltViewModel()
                val picsListState by picsListViewModel.state.collectAsState()
                PicsListScreen(
//                    showPicsList = viewModel::showPicsList,
                    search = viewModel::search,
                    getCollection = viewModel::getCollection,
                    showConnectionError = viewModel::showConnectionError,
                    saveStateSnapshot = picsListViewModel::saveStateSnapshot,
                    toDo = viewModel.onPicsListScreenRefresh,
                    appState = appState,
                    state = picsListState,
                    goToPicScreen = { navController.navigate(NavList.PicScreen.name) },
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) },
                    goBack = { navController.navigateUp() },
                    previousPage = navController.previousBackStackEntry?.destination?.route,
                )
            }
            composable(route = NavList.PicScreen.name) {
                val picViewModel: PicViewModel = hiltViewModel()
                val picState by picViewModel.state.collectAsState() // todo two states??? vv
                val picScreenState by picViewModel.picScreenState.collectAsState()
                PicScreen(
                    search = picViewModel::search,
                    saveStateSnapshot = picViewModel::saveStateSnapshot,
                    getCollection = picViewModel::getCollection,
                    editCollection = picViewModel::editCollection,
                    updateCollectionToSaveTo = picViewModel::updateCollectionToSaveTo,
                    changeFullScreenMode = picViewModel::changeFullScreenMode,
                    showConnectionError = picViewModel::showConnectionError,
                    picScreenState = picScreenState,
                    state = picState,
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) }
                ) { navController.navigate(NavList.AuthScreen.name) }
            }
            composable(route = NavList.SearchScreen.name) {
                val searchViewModel: SearchViewModel = hiltViewModel()
                SearchScreen(
                    search = searchViewModel::search,
                    getAllTags = searchViewModel::getAllTags,
                    getFilteredTags = searchViewModel::getFilteredTags,
                    tags = searchViewModel.tags,
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) },
                )
            }
            composable(route = NavList.AuthScreen.name) {
                AuthScreen(
                    saveStateSnapshot = viewModel::saveStateSnapshot,
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