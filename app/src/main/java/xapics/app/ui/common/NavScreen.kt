package xapics.app.ui.common

import android.annotation.SuppressLint
import android.os.Build
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
import xapics.app.ui.MainViewModel
import xapics.app.R
import xapics.app.ShowHide.HIDE
import xapics.app.ui.auth.AdminScreen
import xapics.app.ui.auth.AuthScreen
import xapics.app.ui.auth.EditFilmsScreen
import xapics.app.ui.auth.EditRollsScreen
import xapics.app.ui.auth.ProfileScreen
import xapics.app.ui.auth.UploadPicsScreen
import xapics.app.ui.common.homeScreen.HomeScreen
import xapics.app.ui.common.picScreen.PicDetails
import xapics.app.ui.common.picScreen.PicScreen
import xapics.app.ui.composables.TopBar
import xapics.app.ui.windowInfo

enum class NavList(@StringRes val title: Int) {
    HomeScreen(title = R.string.home_screen),
    PicsListScreen(title = R.string.pics_list_screen),
    PicScreen(title = R.string.pic_screen),
    SearchScreen(title = R.string.search_screen),
    EditFilmsScreen(title = R.string.edit_films_screen),
    EditRollsScreen(title = R.string.edit_rolls_screen),
    UploadPicsScreen(title = R.string.upload_pics_screen),
    AuthScreen(title = R.string.auth_screen),
    AdminScreen(title = R.string.admin_screen),
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
//                hide(WindowInsetsCompat.Type.navigationBars())
//                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                controller?.apply {
                    show(WindowInsetsCompat.Type.statusBars())
//                show(WindowInsetsCompat.Type.navigationBars())
//                systemBarsBehavior = WindowInsetsController.BEHAVIOR_DEFAULT
                }
            }
        }
    }

    Scaffold (
        modifier = Modifier.pointerInput(appState.searchField) {
            detectTapGestures(onTap = {
                if (appState.searchField.isShown) viewModel.showSearch(HIDE)
            })
        },
        topBar = {
            if (!appState.isFullscreen) {
                TopBar(
                    viewModel = viewModel,
                    appState = appState,
                    popBackStack = { navController.popBackStack() },
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) },
                    goToAdminScreen = { navController.navigate(NavList.AdminScreen.name) },
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
                PicDetails(viewModel, appState) { navController.navigate(NavList.AuthScreen.name) }
            }
        },
    ) { innerPadding ->
        BackHandler(
            enabled = backStackEntry?.destination?.route == "PicsListScreen"
                    || backStackEntry?.destination?.route == "PicScreen"
                    || backStackEntry?.destination?.route == "AdminScreen"
        ) {
            when (backStackEntry?.destination?.route) {
                "AdminScreen" -> {
                    viewModel.getRollsList()
                    viewModel.getAllTags()
                }
                "PicsListScreen" -> {
                    viewModel.loadStateSnapshot()
                    viewModel.showPicsList(HIDE)
                }
                "PicScreen" -> {
                    viewModel.loadStateSnapshot()
                    viewModel.changeFullScreenMode(false)
                }
            }
            navController.popBackStack()
        }

        NavHost(
            navController = navController,
            startDestination = NavList.HomeScreen.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = NavList.HomeScreen.name) {
                HomeScreen(
                    viewModel = viewModel,
                    appState = appState,
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) },
                    goToSearchScreen = { navController.navigate(NavList.SearchScreen.name) },
                )
            }
            composable(route = NavList.PicsListScreen.name) {
                PicsListScreen(
                    viewModel = viewModel,
                    appState = appState,
                    goToPicScreen = { navController.navigate(NavList.PicScreen.name) },
                    popBackStack = { navController.popBackStack() },
                    previousPage = navController.previousBackStackEntry?.destination?.route,
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
            composable(route = NavList.SearchScreen.name) {
                SearchScreen(
                    viewModel = viewModel,
                    appState = appState,
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) },
                )
            }
            composable(route = NavList.EditFilmsScreen.name) {
                EditFilmsScreen(
                    viewModel = viewModel,
                    appState = appState,
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) },
                )
            }
            composable(route = NavList.EditRollsScreen.name) {
                EditRollsScreen(
                    viewModel = viewModel,
                    appState = appState,
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) },
                    goToEditFilmsScreen = { navController.navigate(NavList.EditFilmsScreen.name) }
                )
            }
            composable(route = NavList.UploadPicsScreen.name) {
                UploadPicsScreen(
                    viewModel = viewModel,
                    appState = appState,
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) },
                )
            }
            composable(route = NavList.AuthScreen.name) {
                AuthScreen(
                    viewModel = viewModel,
                    popBackStack = { navController.popBackStack() },
                    goToAdminScreen = { navController.navigate(NavList.AdminScreen.name) },
                    goToProfileScreen = { navController.navigate(NavList.ProfileScreen.name) },
                    isLoading = appState.isLoading,
                )
            }
            composable(route = NavList.AdminScreen.name) {
                AdminScreen(
                    viewModel = viewModel,
                    goToEditFilmsScreen = { navController.navigate(NavList.EditFilmsScreen.name) },
                    goToEditRollsScreen = { navController.navigate(NavList.EditRollsScreen.name) },
                    goToUploadPicsScreen = { navController.navigate(NavList.UploadPicsScreen.name) }
                )
            }
            composable(route = NavList.ProfileScreen.name) {
                ProfileScreen(
                    viewModel = viewModel,
                    appState = appState,
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) },
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) }
                )
            }
        }
    }
}