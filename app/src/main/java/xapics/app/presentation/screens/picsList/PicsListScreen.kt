package xapics.app.presentation.screens.picsList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import xapics.app.Pic
import xapics.app.Screen
import xapics.app.presentation.WindowInfo.WindowType.Compact
import xapics.app.presentation.WindowInfo.WindowType.Medium
import xapics.app.presentation.components.AsyncPic
import xapics.app.presentation.components.ConnectionErrorButton
import xapics.app.presentation.windowInfo

@Composable
fun PicsListScreen(
    isLoading: Boolean,
    search: () -> Unit,
    query: String,
    getCollection: (collection: String, () -> Unit) -> Unit,
    connectionErrorIsShown: Boolean,
    showConnectionError: (Boolean) -> Unit,
    saveCaption: () -> Unit,
    picsList: List<Pic>,
    goToPicScreen: (picIndex: Int) -> Unit,
    goToAuthScreen: () -> Unit,
    goBack: () -> Unit,
    previousPage: String?
) {
    val context = LocalContext.current

    LaunchedEffect(picsList) {
        if (picsList.size == 1) {
            goBack()
            if (previousPage != Screen.Pic.toString()) goToPicScreen(0)
        } else {
            picsList.forEach {// preloading images
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
            connectionErrorIsShown -> {
                ConnectionErrorButton {
//                    if (query.contains(what? collection?)) {
//                        getCollection(query, goToAuthScreen)
//                    } else {
                        search()  // todo merge with getCollection?
//                    }
                    showConnectionError(false)
                }
            }
            picsList.isEmpty() && isLoading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }
            picsList.isEmpty() -> Text("Nothing found :(")
            picsList.size == 1 -> {} // going to PicScreen
            else -> {
                @Composable
                fun picItem(index: Int, modifier: Modifier = Modifier) {
                    val pic = picsList[index]
                    AsyncPic(
                        url = pic.imageUrl,
                        description = pic.description,
                        modifier = modifier.clip(RoundedCornerShape(2.dp)),
                        onClick = {
                            saveCaption()
                            goToPicScreen(index)
                        }
                    )
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
                            count = picsList.size,
                            key = { picsList[it].id },
                        ) {
                            picItem(
                                index = it,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                        if (isLoading && picsList.isNotEmpty()) item {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                CircularProgressIndicator()
                            }
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
                            count = picsList.size,
                            key = { picsList[it].id },
                        ) {
                            picItem(
                                index = it,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                        if (isLoading && picsList.isNotEmpty()) item {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                CircularProgressIndicator()
                            }
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