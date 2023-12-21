package xapics.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import xapics.app.AppState
import xapics.app.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel, appState: AppState, goToPicsListScreen: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        LazyRow {
            items(appState.rollThumbnails?.size ?: 0) {
                RollCard(
                    appState.isLoading,
                    appState.rollThumbnails!![it].first,
                    appState.rollThumbnails[it].second,
                    viewModel::getPicsList,
                    goToPicsListScreen
                )
            }
        }

        /**
        Row {
            Button(
                onClick = {
                    viewModel.getPicsList(film = "Ektachrome")
                    goToPicsListScreen()
                },
            ) {
                Text("Ektachrome")
            }
            Button(
                onClick = {
                    viewModel.getPicsList(film = "Aerocolor")
                    goToPicsListScreen()
                },
            ) {
                Text("Aerocolor")
            }
        }
        Row {
            Button(
                onClick = {
                    viewModel.getPicsList(year = 2021)
                    goToPicsListScreen()
                },
            ) {
                Text("2021")
            }
            Button(
                onClick = {
                    viewModel.getPicsList(year = 2023)
                    goToPicsListScreen()
                },
            ) {
                Text("2023")
            }
        }
         */
    }
}

@Composable
fun RollCard(isLoading: Boolean, imageUrl: String, rollTitle: String, getPicsList: (Int?, String?, String?) -> Unit, goToPicsListScreen: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF333333))
            .padding(1.dp)
            .clip(RoundedCornerShape(15.dp))
            .clickable {
                getPicsList(null, rollTitle, null)
                goToPicsListScreen()
            }
    ) {
        CircularProgressIndicator() // TODO remove?

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Thumbnail of the $rollTitle roll",
                modifier = Modifier
                    .height(100.dp)
                    .width((100 * 1.5).dp)
            )
            Text(text = rollTitle)
        }

        if(isLoading) {
            CircularProgressIndicator()
        }
    }
}