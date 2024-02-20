package xapics.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import xapics.app.AppState
import xapics.app.MainViewModel
import xapics.app.R
import xapics.app.ui.composables.AsyncPic
import xapics.app.ui.composables.ConnectionErrorButton
import xapics.app.ui.composables.RollCard
import xapics.app.ui.composables.PicTagsList

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel, appState: AppState, goToPicsListScreen: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp)
        ) {
            if (appState.showConnectionError) {
                ConnectionErrorButton {
                    viewModel.getRollsList()
                    viewModel.changeConnectionErrorVisibility(false)
                }
            } else {
                appState.pic?.let {

                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp, bottom = 32.dp)
                    ) {
                        val height = (maxWidth.value / 1.5).dp
                        AsyncPic(
                            url = it.imageUrl,
                            description = "random pic: ${it.description}",
                            modifier = Modifier
                                .height(height)
                                .fillMaxWidth()
//                                .clip(RoundedCornerShape(2.dp))
                        ) {
                            viewModel.getRandomPic()
                            // TODO goToPicScreen(), any collection?
                        }
                    }
                }

                val rolls = appState.rollThumbnails?.size ?: 0

                LazyRow {
                    items(rolls) {
                        val imageUrl = appState.rollThumbnails!![it].thumbUrl
                        val rollTitle = appState.rollThumbnails[it].title
                        val currentPage = stringResource(R.string.home_screen)
                        RollCard(
                            width = 150.dp,
                            isLoading = appState.isLoading,
                            imageUrl = imageUrl,
                            rollTitle = rollTitle,
                        ) {
                            viewModel.getPicsList(null, rollTitle, null)
                            goToPicsListScreen()
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                PicTagsList(appState.tags)

                Spacer(modifier = Modifier.weight(1f))
            }

            if (appState.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}