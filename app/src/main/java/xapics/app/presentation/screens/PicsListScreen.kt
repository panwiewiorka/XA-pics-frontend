package xapics.app.presentation.screens

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.imageLoader
import coil.request.ImageRequest
import xapics.app.OnPicsListScreenRefresh
import xapics.app.OnPicsListScreenRefresh.SEARCH
import xapics.app.presentation.AppState
import xapics.app.presentation.WindowInfo.WindowType.Compact
import xapics.app.presentation.WindowInfo.WindowType.Medium
import xapics.app.presentation.composables.AsyncPic
import xapics.app.presentation.composables.ConnectionErrorButton
import xapics.app.presentation.windowInfo

@Composable
fun PicsListScreen(
    showPicsList: (Boolean) -> Unit,
    search: (query: String) -> Unit,
    getCollection: (collection: String, () -> Unit) -> Unit,
    showConnectionError: (Boolean) -> Unit,
    updatePicState: (Int) -> Unit,
    saveStateSnapshot: () -> Unit,
    toDo: Pair<OnPicsListScreenRefresh, String>,
    appState: AppState,
    goToPicScreen: () -> Unit,
    goToAuthScreen: () -> Unit,
    goBack: () -> Unit,
    previousPage: String?
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        showPicsList(true)
    }

    LaunchedEffect(appState.picsList) {
        if (appState.picsList.size == 1) {
            goBack()
            if (previousPage != "PicScreen") goToPicScreen()
        } else {
            appState.picsList.forEach {// preloading images
                val request = ImageRequest.Builder(context)
                    .data(it.imageUrl)
                    // Optional, but setting a ViewSizeResolver will conserve memory by limiting the size the image should be preloaded into memory at.
//            .size(ViewSizeResolver(imageView))
                    .build()
                context.imageLoader.enqueue(request)
            }
        }
    }

    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            appState.showConnectionError -> {
                ConnectionErrorButton {
                    if (toDo.first == SEARCH) {
                        search(toDo.second)
                    } else {
                        getCollection(toDo.second, goToAuthScreen)
                    }
                    showConnectionError(false)
                }
            }
            !appState.showPicsList -> { }
            appState.picsList.isEmpty() && !appState.isLoading -> Text("Nothing found :(")
            appState.picsList.size == 1 -> {} // going to PicScreen
            else -> {
                @Composable
                fun picItem(index: Int, modifier: Modifier = Modifier) {
                    val pic = appState.picsList[index]
                    AsyncPic(
                        url = pic.imageUrl,
                        description = pic.description,
                        modifier = modifier.clip(RoundedCornerShape(2.dp))
                    ) {
                        updatePicState(index)
                        saveStateSnapshot()
                        goToPicScreen()
                    }
                }

                val windowInfo = windowInfo()
                val isPortrait = windowInfo.isPortraitOrientation
                val isCompact = windowInfo.windowType == Compact
                val lowestDimension = if (isPortrait) maxWidth else maxHeight
                val gridCellSize = when (windowInfo.windowType) {
                    Compact -> lowestDimension
                    Medium -> lowestDimension / 2
                    else -> lowestDimension / 3
                }

                if (isPortrait) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(gridCellSize),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            count = appState.picsList.size,
                            key = { appState.picsList[it].id },
                        ) {
                            picItem(
                                index = it,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                        item {
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                } else {
                    LazyHorizontalGrid(
                        rows = GridCells.Adaptive(gridCellSize),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = if (isCompact) 32.dp else 0.dp)
                    ) {
                        items(
                            count = appState.picsList.size,
                            key = { appState.picsList[it].id },
                        ) {
                            picItem(
                                index = it,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                        item {
                            Spacer(Modifier.width(24.dp))
                        }
                    }
                }
            }
        }
    }
}