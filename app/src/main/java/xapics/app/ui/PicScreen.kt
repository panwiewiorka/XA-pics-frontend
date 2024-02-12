package xapics.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import xapics.app.AppState
import xapics.app.MainViewModel
import xapics.app.R
import xapics.app.ui.composables.CollectionsDropDownMenu
import xapics.app.ui.theme.CollectionTag
import xapics.app.ui.theme.DefaultTag
import xapics.app.ui.theme.FilmTag
import xapics.app.ui.theme.YearTag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PicScreen(
    viewModel: MainViewModel, appState: AppState, goToPicsListScreen: () -> Unit, goToAuthScreen: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        if (appState.picIndex != null && appState.picsList != null && appState.pic != null) {

            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {
                val height = (maxWidth.value / 1.5).dp
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(height)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    CircularProgressIndicator()
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(appState.pic.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = appState.pic.description,
                    )
                }
            }

            Row {
                Column {
                    Text(text = "${appState.picIndex + 1} / ${appState.picsList.size}")
                    Text(text = appState.pic.description)
                }
                Spacer(modifier = Modifier.weight(1f))

                val picId = appState.pic.id

                Box {
                    CollectionsDropDownMenu(
                        appState.userCollections,
                        appState.picCollections,
                        appState.collectionToSaveTo,
                        picId,
                        viewModel::editCollection,
                        viewModel::updateCollectionToSaveTo,
                        viewModel::rememberToGetBackAfterLoggingIn,
                    ) { goToAuthScreen() }
                }

                IconButton(
                    onClick = {
                        val isAuthorised = viewModel.editCollection(appState.collectionToSaveTo, picId)
                        if (!isAuthorised) {
                            viewModel.rememberToGetBackAfterLoggingIn(true)
                            goToAuthScreen()
                        }
                    },
                ) {
                    val collection = appState.collectionToSaveTo
                    val picInCollection = appState.picCollections.contains(collection)
                    if(collection == stringResource(R.string.fav_collection)) {
                        if(picInCollection) {
                            Icon (Icons.Filled.Favorite, "Remove from $collection")
                        } else {
                            Icon (Icons.Outlined.FavoriteBorder, "Add to $collection")
                        }
                    } else {
                        if(picInCollection) {
                            Icon (Icons.Filled.Star, "Remove from $collection")
                        } else {
                            Icon(painterResource(id = R.drawable.star_border), "Add to $collection")
                        }
                    }
                }
            }

            Row {
                Button(
                    onClick = { viewModel.updatePicState(appState.picIndex - 1) },
                    enabled = appState.picIndex > 0
                ) {
                    Text("<")
                }
                Button(
                    onClick = { viewModel.updatePicState(appState.picIndex + 1) },
                    enabled = (appState.picIndex < appState.picsList.lastIndex)
                ) {
                    Text(">")
                }
            }
        }

        FlowRow(modifier = Modifier) {
            PicTag(appState.pic?.film ?: "", FilmTag) {
                viewModel.getPicsList(appState.topBarCaption, film = appState.pic?.film)
                goToPicsListScreen()
            }

            PicTag(appState.pic?.year?.toString() ?: "", YearTag) {
                viewModel.getPicsList(appState.topBarCaption, year = appState.pic?.year)
                goToPicsListScreen()
            }

            appState.picCollections.forEach {
                PicTag(text = it, color = CollectionTag) {
                    viewModel.getCollection(appState.topBarCaption, it)
                    goToPicsListScreen()
                }
            }

            val tags = appState.pic?.tags?.split(',')
            tags?.forEach {
                val tag = it.trim()
                PicTag(tag, DefaultTag) {
                    viewModel.getPicsList(appState.topBarCaption, tag = tag)
                    goToPicsListScreen()
                }
            }
        }

        if(appState.isLoading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun PicTag(text: String, color: Color, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .padding(bottom = 8.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .border(1.dp, color, CircleShape)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}