package xapics.app.presentation.screens.home.layouts

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xapics.app.presentation.screens.home.HomeState
import xapics.app.presentation.screens.home.components.RandomPic
import xapics.app.presentation.screens.home.components.TagsCloud
import xapics.app.presentation.screens.home.components.rollCardsGrid

@Composable
fun HomePortraitMediumLayout(
    getRandomPic: () -> Unit,
    homeState: HomeState,
    goToPicsListScreen: (searchQuery: String) -> Unit,
    updateAndGoToPicScreen: () -> Unit,
    goToSearchScreen: () -> Unit,
    maxWidth: Dp,
    padding: Dp,
    tagsScrollState: ScrollState,
    gridState: LazyGridState,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        state = gridState,
        modifier = Modifier.fillMaxSize()
    ) {
        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = padding * 2)
                    .fillMaxWidth()
                    .height(maxWidth / 3)
            ) {
                Row {
                    homeState.randomPic?.let {
                        RandomPic(
                            pic = it,
                            getRandomPic = getRandomPic,
                            updateAndGoToPicScreen = updateAndGoToPicScreen,
                            modifier = Modifier.weight(1f),
                            paddingModifier = Modifier,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(maxWidth / 3)
                    ) {
                        TagsCloud(
                            scrollState = tagsScrollState,
                            tags = homeState.tags,
                            goToPicsListScreen = goToPicsListScreen,
                            goToSearchScreen = goToSearchScreen,
                            padding = padding
                        )
                    }
                }

                if (homeState.tags.isNotEmpty() && !homeState.rollThumbnails.isNullOrEmpty()) {
                    HorizontalDivider(modifier = Modifier.align(Alignment.TopCenter))
                    HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
                }
            }
        }

        rollCardsGrid(homeState.rollThumbnails, goToPicsListScreen, true, Modifier.padding(padding))
    }
}