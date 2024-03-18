package xapics.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xapics.app.AppState
import xapics.app.MainViewModel
import xapics.app.ShowHide.HIDE
import xapics.app.Tag
import xapics.app.TagState
import xapics.app.ui.composables.AsyncPic
import xapics.app.ui.composables.ConnectionErrorButton
import xapics.app.ui.composables.PicTag
import xapics.app.ui.composables.RollCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel, appState: AppState, goToPicsListScreen: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 4.dp)
    ) {
        when {
            appState.connectionError.isShown -> {
                ConnectionErrorButton {
                    viewModel.authenticate()
                    viewModel.getUserInfo {} // TODO needed?
//                        getPicsList(2020)
                    viewModel.getRollsList()
                    viewModel.getRandomPic()
                    viewModel.getAllTags()

                    viewModel.showConnectionError(HIDE)
                }
            }
            else -> {
                val rolls = appState.rollThumbnails?.size ?: 0

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    item(
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
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
                                ) {
                                    viewModel.getRandomPic()
                                    // TODO goToPicScreen(), caption: Random pic, any collection?
                                }
                            }
                        }
                    }
                    items(rolls) {
                        val imageUrl = appState.rollThumbnails!![it].thumbUrl
                        val rollTitle = appState.rollThumbnails[it].title
                        RollCard(
                            width = 150.dp,
                            isLoading = appState.isLoading,
                            imageUrl = imageUrl,
                            rollTitle = rollTitle,
                        ) {
                            viewModel.search("roll = $rollTitle")
                            goToPicsListScreen()
                        }
                    }

                    item(
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        Divider(modifier = Modifier.padding(vertical = 16.dp))
                    }

                    item(
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        FlowRow(
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            val tags = appState.tags.map {
                                Tag(
                                    it.type,
                                    it.value,
                                    TagState.ENABLED
                                )
                            }
                            tags.forEach {
                                PicTag(it, viewModel::getTagColorAndName) {
                                    viewModel.search("${it.type} = ${it.value}")
                                    goToPicsListScreen()
                                }
                            }
                        }
                    }
                }

                if (appState.isLoading) CircularProgressIndicator()
            }
        }
    }
}