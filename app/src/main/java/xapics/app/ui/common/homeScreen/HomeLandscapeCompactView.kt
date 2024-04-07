package xapics.app.ui.common.homeScreen

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
import xapics.app.ui.AppState
import xapics.app.ui.MainViewModel
import xapics.app.Tag
import xapics.app.TagState
import xapics.app.ui.composables.AsyncPic
import xapics.app.ui.composables.PicTag
import xapics.app.ui.composables.RollCard

@Composable
fun HomeLandscapeCompactView(
    viewModel: MainViewModel,
    appState: AppState,
    goToPicsListScreen: () -> Unit,
    padding: Dp,
) {
    val rolls = appState.rollThumbnails?.size ?: 0
    var tagsWidth by remember { mutableStateOf(0.dp) }
    var tagHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box {
        BoxWithConstraints(
            modifier = Modifier
                .onSizeChanged { tagHeight = with(density) {it.height.toDp()} }
                .alpha(0f)
        ) {
            PicTag(tag = Tag("hashtag", "test tag")){}
        }

        BoxWithConstraints(
            modifier = Modifier
                .onSizeChanged { tagsWidth = with(density) {it.width.toDp()} }
                .alpha(0f)
        ) {
            LazyHorizontalStaggeredGrid(
                rows = StaggeredGridCells.FixedSize(tagHeight),
                verticalArrangement = Arrangement.Center,
            ) {
                val tags = appState.tags.map {
                    Tag(
                        it.type,
                        it.value,
                        TagState.ENABLED
                    )
                }
                items(tags.size) {
                    PicTag(tags[it]) {
                        viewModel.search("${tags[it].type} = ${tags[it].value}")
                        goToPicsListScreen()
                    }
                }
            }
        }

        LazyHorizontalGrid(
            rows = GridCells.Adaptive(100.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(padding),
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding)
        ) {
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                appState.pic?.let {
                    AsyncPic(
                        url = it.imageUrl,
                        description = "random pic: ${it.description}",
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(start = 8.dp, end = 32.dp, top = 1.dp)
                    ) {
                        viewModel.getRandomPic()
                        // TODO goToPicScreen(), caption: Random pic, any collection?
                    }
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
                ) {
                    viewModel.search("roll = $rollTitle")
                    goToPicsListScreen()
                }
            }

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
                        val tags = appState.tags.map {
                            Tag(
                                it.type,
                                it.value,
                                TagState.ENABLED
                            )
                        }
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