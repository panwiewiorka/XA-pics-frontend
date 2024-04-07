package xapics.app.ui.common.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xapics.app.ui.AppState
import xapics.app.ui.MainViewModel
import xapics.app.ui.composables.AsyncPic
import xapics.app.ui.composables.RollCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeLandscapeMediumView(
    viewModel: MainViewModel,
    appState: AppState,
    goToPicsListScreen: () -> Unit,
    goToSearchScreen: () -> Unit,
    maxHeight: Dp,
    padding: Dp,
) {
    val rolls = appState.rollThumbnails?.size ?: 0
    val scrollState = rememberScrollState()

    LazyHorizontalGrid(
        rows = GridCells.Adaptive(100.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
    ) {
        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = padding * 2)
                    .width((maxHeight.value * 0.75).dp)
            ) {
                Column {
                    appState.pic?.let {
                        AsyncPic(
                            url = it.imageUrl,
                            description = "random pic: ${it.description}",
                        ) {
                            viewModel.getRandomPic()
                            // TODO goToPicScreen(), caption: Random pic, any collection?
                        }
                    }

                    Box {
                        TagsCloud(scrollState, appState.tags, viewModel::search, goToPicsListScreen, goToSearchScreen, padding)
                    }
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.tertiary)
                        .align(Alignment.CenterEnd)
                ) {}
            }
        }

        items(rolls) {
            val imageUrl = appState.rollThumbnails!![it].thumbUrl
            val rollTitle = appState.rollThumbnails[it].title
            RollCard(
                isLoading = appState.isLoading,
                imageUrl = imageUrl,
                rollTitle = rollTitle,
                isPortrait = false,
                modifier = Modifier.padding(bottom = padding)
            ) {
                viewModel.search("roll = $rollTitle")
                goToPicsListScreen()
            }
        }
    }
}