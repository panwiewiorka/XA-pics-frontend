package xapics.app.presentation.screens.home.layouts

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xapics.app.presentation.AppState
import xapics.app.presentation.screens.home.components.RandomPic
import xapics.app.presentation.screens.home.components.TagsCloud
import xapics.app.presentation.screens.home.components.rollCardsGrid

@Composable
fun HomeLandscapeMediumLayout(
    getRandomPic: () -> Unit,
    appState: AppState,
    goToPicsListScreen: (searchQuery: String) -> Unit,
    updateAndGoToPicScreen: () -> Unit,
    goToSearchScreen: () -> Unit,
    maxHeight: Dp,
    padding: Dp,
    tagsScrollState: ScrollState,
    gridState: LazyGridState,
) {
    LazyHorizontalGrid(
        rows = GridCells.Adaptive(100.dp),
        horizontalArrangement = Arrangement.spacedBy(padding * 2),
        verticalArrangement = Arrangement.SpaceBetween,
        state = gridState,
        modifier = Modifier.fillMaxSize()
    ) {
        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = padding)
                    .width((maxHeight.value * 0.75).dp)
            ) {
                Column {
                    appState.randomPic?.let {
                        RandomPic(
                            pic = it,
                            getRandomPic = getRandomPic,
                            updateAndGoToPicScreen = updateAndGoToPicScreen,
                            modifier = Modifier,
                            paddingModifier = Modifier
                        )
                    }

                    Box {
                        TagsCloud(
                            scrollState = tagsScrollState,
                            tags = appState.tags,
                            goToPicsListScreen = goToPicsListScreen,
                            goToSearchScreen = goToSearchScreen,
                            padding = padding
                        )
                    }
                }

                if (appState.tags.isNotEmpty() && !appState.rollThumbnails.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.tertiary)
                            .align(Alignment.CenterEnd)
                    ) {}
                }
            }
        }

        rollCardsGrid(
            rollThumbnails = appState.rollThumbnails,
            goToPicsListScreen = goToPicsListScreen,
            isPortrait = false,
            modifier = Modifier.padding(bottom = padding)
        )

        item {
            Spacer(modifier = Modifier.width(1.dp))
        }
    }
}