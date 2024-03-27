package xapics.app.ui.common.homeScreen

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xapics.app.AppState
import xapics.app.MainViewModel
import xapics.app.Tag
import xapics.app.TagState
import xapics.app.ui.composables.AsyncPic
import xapics.app.ui.composables.PicTag
import xapics.app.ui.composables.RollCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomePortraitCompactView(viewModel: MainViewModel, appState: AppState, goToPicsListScreen: () -> Unit) {
    val rolls = appState.rollThumbnails?.size ?: 0

    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 4.dp)
    ) {
        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            appState.pic?.let {
                AsyncPic(
                    url = it.imageUrl,
                    description = "random pic: ${it.description}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
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
                isPortrait = true,
                modifier = Modifier.padding(12.dp)
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
                    PicTag(it) {
                        viewModel.search("${it.type} = ${it.value}")
                        goToPicsListScreen()
                    }
                }
            }
        }
    }
}