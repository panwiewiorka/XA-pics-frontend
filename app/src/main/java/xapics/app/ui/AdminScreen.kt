package xapics.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xapics.app.MainViewModel
import xapics.app.Thumb

@Composable
fun AdminScreen(
    viewModel: MainViewModel,
    goToAuthScreen: () -> Unit,
    goToEditFilmsScreen: () -> Unit,
    goToUploadScreen: () -> Unit,
    goToPicsListScreen: () -> Unit,
){

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