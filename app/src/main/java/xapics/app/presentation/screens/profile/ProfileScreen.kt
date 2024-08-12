package xapics.app.presentation.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import xapics.app.Thumb
import xapics.app.presentation.components.ConnectionErrorButton
import xapics.app.presentation.screens.profile.components.UserView

@Composable
fun ProfileScreen(
    updateUserCollections: (userCollections: List<Thumb>?) -> Unit,
    getUserInfo: (onAuthError: () -> Unit) -> Unit,
    showConnectionError: (Boolean) -> Unit,
    getCollection: (String, () -> Unit) -> Unit,
    renameOrDeleteCollection: (String, String?, () -> Unit) -> Unit,
    profileState: ProfileState,
    goToAuthScreen: () -> Unit,
    goToPicsListScreen: (searchQuery: String) -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        updateUserCollections(null)
        getUserInfo(goToAuthScreen)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            profileState.connectionError -> {
                ConnectionErrorButton {
                    showConnectionError(false)
                    getUserInfo(goToAuthScreen)
                }
            }
//            appState.isLoading -> {
//                CircularProgressIndicator()
//            }
            profileState.userCollections != null -> {
                UserView(
                    profileState.userCollections,
                    getCollection,
                    goToPicsListScreen,
                    goToAuthScreen,
                    renameOrDeleteCollection,
                    context
                )
            }
        }
    }
}