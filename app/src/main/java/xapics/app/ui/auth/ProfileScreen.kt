package xapics.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xapics.app.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    goToAuthScreen: () -> Unit,
    goToEditFilmsScreen: () -> Unit,
    goToUploadScreen: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
//        when (val userId = viewModel.authState.userId) {
//            null -> goToAuthScreen()
//            0 -> AdminView(viewModel, goToEditFilmsScreen, goToUploadScreen)
//            else -> UserView(userId)
//        }
        AdminView(viewModel, goToEditFilmsScreen, goToUploadScreen)
    }
}


@Composable
fun UserView(userId: Int?) {
    Text("Username = $userId")
}


@Composable
fun AdminView(viewModel: MainViewModel, goToEditFilmsScreen: () -> Unit, goToUploadScreen: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Button(
            onClick = {
                viewModel.getFilmsList()
                goToEditFilmsScreen()
            },
        ) {
            Text("Edit films")
        }
        Button(
            onClick = {
//                viewModel.getFilmsList()
                viewModel.getRollsList()
                goToUploadScreen()
            },
        ) {
            Text("Edit rolls / upload photos")
        }
    }
}