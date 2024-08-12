package xapics.app.presentation.screens.home.layouts

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
import xapics.app.presentation.screens.home.HomeState
import xapics.app.presentation.components.PicTag
import xapics.app.presentation.screens.home.components.RandomPic
import xapics.app.presentation.screens.home.components.rollCardsGrid

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomePortraitCompactLayout(
    getRandomPic: () -> Unit,
    homeState: HomeState,
    goToPicsListScreen: (searchQuery: String) -> Unit,
    updateAndGoToPicScreen: () -> Unit,
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
        homeState.randomPic?.let {
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Box(
                    modifier = Modifier.height((maxWidth.value / 1.5).dp)
                ) {
                    RandomPic(
                        pic = it,
                        getRandomPic = getRandomPic,
                        updateAndGoToPicScreen = updateAndGoToPicScreen,
                        modifier = Modifier.fillMaxWidth(),
                        paddingModifier = Modifier
                    )
                }
            }
        }

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Spacer(modifier = Modifier.height(24.dp))
        }

        rollCardsGrid(
            rollThumbnails = homeState.rollThumbnails,
            goToPicsListScreen = goToPicsListScreen,
            isPortrait = true,
            modifier = Modifier.padding(padding)
        )

        if (homeState.tags.isNotEmpty() && !homeState.rollThumbnails.isNullOrEmpty()) {
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
                val tags = homeState.tags.map {
                    Tag(it.type, it.value, TagState.ENABLED)
                }
                tags.forEach {
                    PicTag(it) {
                        goToPicsListScreen("${it.type} = ${it.value}")
                    }
                }
            }
        }
    }
}