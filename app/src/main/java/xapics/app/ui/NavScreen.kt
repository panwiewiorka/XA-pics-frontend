package xapics.app.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import xapics.app.MainViewModel
import xapics.app.R

enum class NavList(@StringRes val title: Int) {
    HomeScreen(title = R.string.home_screen),
    PicsListScreen(title = R.string.pics_list_screen),
    PicScreen(title = R.string.pic_screen),
    EditFilmsScreen(title = R.string.edit_films_screen),
    UploadScreen(title = R.string.upload_screen)
}

@Composable
fun NavScreen(
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState
) {

    val viewModel: MainViewModel = hiltViewModel()
    val appState by viewModel.state.collectAsState()

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavList.HomeScreen.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = NavList.HomeScreen.name) {
                HomeScreen(
                    viewModel,
                    appState,
                    goToPicsListScreen = { navController.navigate(NavList.PicsListScreen.name) },
                    goToEditFilmsScreen = { navController.navigate(NavList.EditFilmsScreen.name) },
                    goToUploadScreen = { navController.navigate(NavList.UploadScreen.name) }
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
                    goToFilmScreen = { navController.navigate(NavList.PicsListScreen.name) }
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