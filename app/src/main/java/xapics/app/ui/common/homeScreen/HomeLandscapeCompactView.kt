package xapics.app.ui.common.homeScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xapics.app.Tag
import xapics.app.TagState
import xapics.app.ui.AppState
import xapics.app.ui.MainViewModel
import xapics.app.ui.composables.PicTag

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HomeLandscapeCompactView(
    viewModel: MainViewModel,
    appState: AppState,
    goToPicsListScreen: () -> Unit,
    goToPicScreen: () -> Unit,
    padding: Dp,
    gridState: LazyGridState,
) {
    var tagsWidth by remember { mutableStateOf(0.dp) }
    var tagHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val tags = appState.tags.map {
        Tag(it.type, it.value, TagState.ENABLED)
    }

    Box {
        // pre-calculating the size of tag composable
        BoxWithConstraints(
            modifier = Modifier
                .onSizeChanged { tagHeight = with(density) {it.height.toDp()} }
                .alpha(0f)
        ) {
            PicTag(tag = Tag("hashtag", "test tag")){}
        }

        // pre-calculating the size of tags cloud
        BoxWithConstraints(
            modifier = Modifier
                .onSizeChanged { tagsWidth = with(density) {it.width.toDp()} }
                .alpha(0f)
        ) {
            LazyHorizontalStaggeredGrid(
                rows = StaggeredGridCells.FixedSize(tagHeight),
                verticalArrangement = Arrangement.Center,
            ) {
                items(tags.size) {
                    PicTag(tags[it]) {}
                }
            }
        }

        LazyHorizontalGrid(
            rows = GridCells.Adaptive(100.dp),
            horizontalArrangement = Arrangement.spacedBy(padding * 2),
            verticalArrangement = Arrangement.spacedBy(padding),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding)
        ) {
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                RandomPic(
                    pic = appState.pic,
                    getRandomPic = viewModel::getRandomPic,
                    updateAndGoToPicScreen = goToPicScreen,
                    modifier = Modifier.fillMaxHeight(),
                    paddingModifier = Modifier.padding(start = 8.dp, end = 32.dp, top = 1.dp)
                )
            }

            rollCardsGrid(appState, viewModel::search, goToPicsListScreen, false, Modifier)

            if (appState.tags.isNotEmpty() && !appState.rollThumbnails.isNullOrEmpty()) {
                item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .fillMaxHeight()
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.tertiary)
                    )
                }
            }

            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = padding)
                        .width(tagsWidth)
                ) {
                    LazyHorizontalStaggeredGrid(
                        rows = StaggeredGridCells.FixedSize(tagHeight),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        items(tags.size) {
                            PicTag(tags[it]) {
                                viewModel.search("${tags[it].type} = ${tags[it].value}")
                                goToPicsListScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}