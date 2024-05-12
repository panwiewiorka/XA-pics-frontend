package xapics.app.ui.auth.profileScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import xapics.app.ui.AppState
import xapics.app.ui.MainViewModel
import xapics.app.ui.composables.ConnectionErrorButton

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
//        if (appState.userName != null) viewModel.getUserInfo{}
    }

    if (appState.userCollections != null) {
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
                else -> {
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
}