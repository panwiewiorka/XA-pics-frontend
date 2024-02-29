package xapics.app.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
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
import xapics.app.TAG
import xapics.app.ui.composables.AsyncPic
import xapics.app.ui.composables.ConnectionErrorButton
import xapics.app.ui.composables.RollCard
import xapics.app.ui.composables.PicTagsList

@Composable
fun HomeScreen(
    viewModel: MainViewModel, appState: AppState, goToPicsListScreen: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 4.dp)
    ) {
        when {
            appState.showConnectionError -> {
                ConnectionErrorButton {
                    viewModel.authenticate()
                    viewModel.getUserInfo {} // TODO needed?
//                        getPicsList(2020)
                    viewModel.getRollsList()
                    viewModel.getRandomPic()
                    viewModel.getAllTags()

                    viewModel.changeConnectionErrorVisibility(false)
                }
            }
            appState.isLoading -> {
                CircularProgressIndicator()
            }
            else -> {
                appState.pic?.let {

                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp, bottom = 32.dp)
                    ) {
                        val height = (maxWidth.value / 1.5).dp
                        Log.d(TAG, "HomeScreen: ${it.imageUrl}")
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
                        RollCard(
                            width = 150.dp,
                            isLoading = appState.isLoading,
                            imageUrl = imageUrl,
                            rollTitle = rollTitle,
                        ) {
                            viewModel.getPicsList("roll = $rollTitle")
                            goToPicsListScreen()
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                PicTagsList(appState.tags, viewModel::getPicsList, goToPicsListScreen)

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}