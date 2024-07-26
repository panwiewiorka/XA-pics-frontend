package xapics.app.presentation.auth.profileScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import xapics.app.presentation.AppState
import xapics.app.presentation.MainViewModel
import xapics.app.presentation.composables.ConnectionErrorButton

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    appState: AppState,
    goToAuthScreen: () -> Unit,
    goToPicsListScreen: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.updateUserCollections(null)
        if (appState.userName != null) viewModel.getUserInfo(goToAuthScreen)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            appState.showConnectionError -> {
                ConnectionErrorButton {
                    viewModel.showConnectionError(false)
                    viewModel.getUserInfo(goToAuthScreen)
                }
            }
//            appState.isLoading -> {
//                CircularProgressIndicator()
//            }
            appState.userCollections != null -> {
                UserView(
                    appState.userCollections,
                    viewModel::getCollection,
                    goToPicsListScreen,
                    goToAuthScreen,
                    viewModel::renameOrDeleteCollection,
                    context
                )
            }
        }
    }
}