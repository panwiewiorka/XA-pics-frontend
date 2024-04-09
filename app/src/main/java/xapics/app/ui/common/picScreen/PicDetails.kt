package xapics.app.ui.common.picScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import xapics.app.ui.AppState
import xapics.app.ui.MainViewModel
import xapics.app.R
import xapics.app.ui.composables.CollectionsDropDownMenu
import xapics.app.ui.windowInfo

@Composable
fun PicDetails(
    viewModel: MainViewModel,
    appState: AppState,
    goToAuthScreen: () -> Unit
) {
    if (appState.picIndex != null && appState.picsList != null && appState.pic != null) { // TODO
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 32.dp, end = 16.dp)
                    .then(
                        if (!windowInfo().isPortraitOrientation) Modifier.width(appState.picDetailsWidth) else Modifier
                    )
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "${appState.picIndex + 1} / ${appState.picsList.size}")
                    Text(
                        text = appState.pic.description,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box {
                    CollectionsDropDownMenu(
                        appState.userCollections,
                        appState.picCollections,
                        appState.collectionToSaveTo,
                        appState.pic.id,
                        viewModel::editCollection,
                        viewModel::updateCollectionToSaveTo,
                    ) { goToAuthScreen() }
                }

                IconButton(
                    onClick = {
                        viewModel.editCollection(
                            appState.collectionToSaveTo,
                            appState.pic.id,
                            goToAuthScreen
                        )
                    },
                ) {
                    val collection = appState.collectionToSaveTo
                    val picInCollection = appState.picCollections.contains(collection)
                    if (collection == stringResource(R.string.fav_collection)) {
                        if (picInCollection) {
                            Icon(Icons.Filled.Favorite, "Remove from $collection")
                        } else {
                            Icon(Icons.Outlined.FavoriteBorder, "Add to $collection")
                        }
                    } else {
                        if (picInCollection) {
                            Icon(Icons.Filled.Star, "Remove from $collection")
                        } else {
                            Icon(
                                painterResource(id = R.drawable.star_border),
                                "Add to $collection"
                            )
                        }
                    }
                }
            }
        }
    }
}