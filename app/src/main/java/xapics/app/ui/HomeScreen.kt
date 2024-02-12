package xapics.app.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import xapics.app.AppState
import xapics.app.MainViewModel
import xapics.app.R
import xapics.app.ui.composables.RollCard
import xapics.app.ui.theme.PicBG

@Composable
fun HomeScreen(
    viewModel: MainViewModel, appState: AppState, goToPicsListScreen: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (appState.isLoading) {
            CircularProgressIndicator()
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp)
        ) {
            val rolls = appState.rollThumbnails?.size ?: 0

            if (rolls == 0 && !appState.isLoading) {
                Text(text = "No connection to server")
                IconButton(onClick = { viewModel.getRollsList() }) {
                    Icon(painterResource(R.drawable.baseline_refresh_24), contentDescription = "refresh")
                }
            } else {
                LazyRow {
                    items(rolls) {
                        val imageUrl = appState.rollThumbnails!![it].thumbUrl
                        val rollTitle = appState.rollThumbnails[it].title
                        val currentPage = stringResource(R.string.home_screen)
                        RollCard(
                            width = 150.dp, // FIXME
                            isLoading = appState.isLoading,
                            imageUrl = imageUrl,
                            rollTitle = rollTitle,
                        ) {
                            viewModel.getPicsList(currentPage, null, rollTitle, null)
                            goToPicsListScreen()
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}