package xapics.app.ui.common.homeScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xapics.app.Tag
import xapics.app.TagState
import xapics.app.ui.AppState
import xapics.app.ui.MainViewModel
import xapics.app.ui.composables.PicTag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomePortraitCompactView(
    viewModel: MainViewModel,
    appState: AppState,
    goToPicsListScreen: () -> Unit,
    goToPicScreen: () -> Unit,
    maxWidth: Dp,
    padding: Dp,
    gridState: LazyGridState,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        state = gridState,
        modifier = Modifier
            .fillMaxSize()
    ) {
        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Box(
                modifier = Modifier.height((maxWidth.value / 1.5).dp)
            ) {
                RandomPic(
                    pic = appState.pic,
                    getRandomPic = viewModel::getRandomPic,
                    updateAndGoToPicScreen = goToPicScreen,
                    modifier = Modifier.fillMaxWidth(),
                    paddingModifier = Modifier
                )
            }
        }

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Spacer(modifier = Modifier.height(24.dp))
        }

        rollCardsGrid(appState, viewModel::search, goToPicsListScreen, true, modifier = Modifier.padding(padding))

        if (appState.tags.isNotEmpty() && !appState.rollThumbnails.isNullOrEmpty()) {
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            }
        }

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            FlowRow(
                modifier = Modifier
                    .padding(horizontal = padding)
                    .padding(bottom = 8.dp)
            ) {
                val tags = appState.tags.map {
                    Tag(it.type, it.value, TagState.ENABLED)
                }
                tags.forEach {
                    PicTag(it) {
                        viewModel.search("${it.type} = ${it.value}")
                        goToPicsListScreen()
                    }
                }
            }
        }
    }
}