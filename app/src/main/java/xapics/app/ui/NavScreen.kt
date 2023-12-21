package xapics.app.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import xapics.app.MainViewModel
import xapics.app.R
import xapics.app.ui.auth.AuthScreen
import xapics.app.ui.auth.ProfileScreen

enum class NavList(@StringRes val title: Int) {
    HomeScreen(title = R.string.home_screen),
    PicsListScreen(title = R.string.pics_list_screen),
    PicScreen(title = R.string.pic_screen),
    EditFilmsScreen(title = R.string.edit_films_screen),
    UploadScreen(title = R.string.upload_screen),
    AuthScreen(title = R.string.auth_screen),
    ProfileScreen(title = R.string.profile_screen)
}

@Composable
fun NavScreen(
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState
) {

    val viewModel: MainViewModel = hiltViewModel()
    val appState by viewModel.appState.collectAsState()

    Scaffold (
        topBar = {
            TopBar(
                goToHomeScreen = { navController.navigate(NavList.HomeScreen.name) },
                goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) }
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
                    goToProfileScreen = { navController.navigate(NavList.ProfileScreen.name) },
                )
            }
            composable(route = NavList.ProfileScreen.name) {
                ProfileScreen(
                    viewModel,
                    goToAuthScreen = { navController.navigate(NavList.AuthScreen.name) },
                    goToEditFilmsScreen = { navController.navigate(NavList.EditFilmsScreen.name) },
                    goToUploadScreen = { navController.navigate(NavList.UploadScreen.name) }
                )
            }
            /**
            composable(route = NavList.SideDish.name) {
                SideDishMenuScreen(
                    onNextButtonClicked = { navController.navigate(NavList.Accompaniment.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    options = sideDishMenuItems,
                    onSelectionChanged = { viewModel.updateSideDish(it) }
                )
            }
            composable(route = NavList.Accompaniment.name) {
                AccompanimentMenuScreen(
                    onNextButtonClicked = { navController.navigate(NavList.Checkout.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    options = accompanimentMenuItems,
                    onSelectionChanged = { viewModel.updateAccompaniment(it) }
                )
            }
            composable(route = NavList.Checkout.name) {
                ///val context = LocalContext.current
                CheckoutScreen(
                    orderUiState = uiState,
                    onNextButtonClicked = { navController.navigate(NavList.Start.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    }
                )
            }
            */
        }
    }
}


@Composable
fun TopBar(
    goToHomeScreen: () -> Unit,
    goToAuthScreen: () -> Unit
) {
    Row {
        IconButton(onClick = goToHomeScreen) {
            Icon(Icons.Outlined.ArrowBack, "Go to Home screen")
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Default.Search, "Search photos")
        }
        Spacer(modifier = Modifier.width(12.dp))
        IconButton(onClick = goToAuthScreen) {
            Icon(Icons.Outlined.AccountCircle, "Go to Profile screen")
        }
    }
}