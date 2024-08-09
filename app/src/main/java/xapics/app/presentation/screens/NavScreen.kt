package xapics.app.presentation.screens

//import xapics.app.NavList
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
import xapics.app.Screen
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
//    val state by viewModel.state.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route?.substringAfterLast('.') ?: "XA pics"
    val prevScreen = navController.previousBackStackEntry?.destination?.route?.substringAfterLast('.') ?: "XA pics"

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
                    showSearch = viewModel::showSearch,
                    loadStateSnapshot = viewModel::loadStateSnapshot,
                    logOut = topBarViewModel::logOut,
                    appState = appState,
                    state = topBarState,
                    goBack = { navController.navigateUp() },
                    goToAuthScreen = { navController.navigate(Screen.Auth) {
                        popUpTo(Screen.Home)
                    } },
                    goToProfileScreen = { navController.navigate(Screen.Profile) },
                    goToPicsListScreen = { searchQuery -> navController.navigate(Screen.PicsList(searchQuery)) },
                    goToSearchScreen = { navController.navigate(Screen.Search) },
                    page = currentScreen,
                    previousPage = prevScreen,
                )
            }
        },
    ) { innerPadding ->
        BackHandler(
            enabled = backStackEntry?.destination?.route == Screen.PicsList.toString()
                    || backStackEntry?.destination?.route == Screen.Pic.toString()
        ) {
            viewModel.loadStateSnapshot()

            when (backStackEntry?.destination?.route) {
                Screen.Pic.toString() -> viewModel.changeFullScreenMode(false)
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
                        viewModel.saveStateSnapshot(replaceExisting = false, picsList = listOf(appState.randomPic!!), picIndex = 0, topBarCaption = "Random pic")
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
                    saveStateSnapshot = picsListViewModel::saveStateSnapshot,
                    picsList = picsListViewModel.picsList,
                    goToPicScreen = { picIndex -> navController.navigate(Screen.Pic(picIndex)) },
                    goToAuthScreen = { navController.navigate(Screen.Auth) },
                    goBack = { navController.navigateUp() },
                    previousPage = navController.previousBackStackEntry?.destination?.route,
                )
            }
            composable<Screen.Pic> {
                val picViewModel: PicViewModel = hiltViewModel()
                val picScreenState by picViewModel.picScreenState.collectAsState()
                PicScreen(
                    saveStateSnapshot = picViewModel::saveStateSnapshot,
                    getCollection = picViewModel::getCollection,
                    editCollection = picViewModel::editCollection,
                    updateCollectionToSaveTo = picViewModel::updateCollectionToSaveTo,
                    changeFullScreenMode = picViewModel::changeFullScreenMode,
                    showConnectionError = picViewModel::showConnectionError,
                    picScreenState = picScreenState,
                    goToPicsListScreen = { searchQuery -> navController.navigate(Screen.PicsList(searchQuery)) }
                ) { navController.navigate(Screen.Auth) }
            }
            composable<Screen.Search> {
                val searchViewModel: SearchViewModel = hiltViewModel()
                SearchScreen(
                    search = searchViewModel::search,
                    getAllTags = searchViewModel::getAllTags,
                    getFilteredTags = searchViewModel::getFilteredTags,
                    tags = searchViewModel.tags,
                    goToPicsListScreen = { searchQuery -> navController.navigate(Screen.PicsList(searchQuery)) },
                )
            }
            composable<Screen.Auth> {
                AuthScreen(
                    saveStateSnapshot = viewModel::saveStateSnapshot,
                    updateUserName = viewModel::updateUserName,
                    rememberToGetBackAfterLoggingIn = viewModel::rememberToGetBackAfterLoggingIn,
                    signUpOrIn = viewModel::signUpOrIn,
                    authResults = viewModel.authResults,
                    getBackAfterLoggingIn = appState.getBackAfterLoggingIn,
                    goBack = { navController.navigateUp() },
                    goToProfileScreen = { navController.navigate(Screen.Profile) },
                    isLoading = appState.isLoading,
                )
            }
            composable<Screen.Profile> {
                ProfileScreen(
                    updateUserCollections = viewModel::updateUserCollections,
                    getUserInfo = viewModel::getUserInfo,
                    showConnectionError = viewModel::showConnectionError,
                    getCollection = viewModel::getCollection,
                    renameOrDeleteCollection = viewModel::renameOrDeleteCollection,
                    appState = appState,
                    goToAuthScreen = { navController.navigate(Screen.Auth) },
                    goToPicsListScreen = { searchQuery -> navController.navigate(Screen.PicsList(searchQuery)) }
                )
            }
        }
    }
}