package xapics.app.ui.common.homeScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xapics.app.AppState
import xapics.app.MainViewModel
import xapics.app.ui.composables.AsyncPic
import xapics.app.ui.composables.RollCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomePortraitMediumView(
    viewModel: MainViewModel,
    appState: AppState,
    goToPicsListScreen: () -> Unit,
    goToSearchScreen: () -> Unit,
    maxWidth: Dp,
    padding: Dp
) {
    val rolls = appState.rollThumbnails?.size ?: 0
    val scrollState = rememberScrollState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth()
                    .height(maxWidth / 3)
            ) {
                Row {
                    appState.pic?.let {
                        AsyncPic(
                            url = it.imageUrl,
                            description = "random pic: ${it.description}",
                            modifier = Modifier.weight(1f)
                        ) {
                            viewModel.getRandomPic()
                            // TODO goToPicScreen(), caption: Random pic, any collection?
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(maxWidth / 3)
                    ) {
                        TagsCloud(scrollState, appState.tags, viewModel::search, goToPicsListScreen, goToSearchScreen, padding)
                    }
                }

                Divider(modifier = Modifier.align(Alignment.TopCenter))
                Divider(modifier = Modifier.align(Alignment.BottomCenter))
            }
        }

        items(rolls) {
            val imageUrl = appState.rollThumbnails!![it].thumbUrl
            val rollTitle = appState.rollThumbnails[it].title
            RollCard(
                isLoading = appState.isLoading,
                imageUrl = imageUrl,
                rollTitle = rollTitle,
                isPortrait = true,
                modifier = Modifier.padding(padding)
            ) {
                viewModel.search("roll = $rollTitle")
                goToPicsListScreen()
            }
        }
    }
}