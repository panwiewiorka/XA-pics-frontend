package xapics.app.presentation.screens.picScreen.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xapics.app.Pic
import xapics.app.R
import xapics.app.TAG
import xapics.app.data.db.StateSnapshot
import xapics.app.presentation.AppState
import xapics.app.presentation.components.CollectionsDropDownMenu
import xapics.app.presentation.windowInfo

@Composable
fun PicDetails(
    search: (query: String) -> Unit,
    saveStateSnapshot: (
        replaceExisting: Boolean,
        picsList: List<Pic>?,
        pic: Pic?,
        picIndex: Int?,
        topBarCaption: String?
    ) -> Unit,
    getCollection: (collection: String, () -> Unit) -> Unit,
    editCollection: (collection: String, picId: Int, onAuthError: () -> Unit) -> Unit,
    updateCollectionToSaveTo:(String) -> Unit,
    blurContent: (Boolean) -> Unit,
    appState: AppState,
    state: StateSnapshot,
    picDetailsWidth: Dp,
    goToAuthScreen: () -> Unit,
    goToPicsListScreen: () -> Unit,
) {
    var showTags by remember { mutableStateOf(false) }
    
    if (showTags) {
        AlertDialog(
            onDismissRequest = {
                showTags = false
                blurContent(false)
                               },
            title = {
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = state.pic?.description ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        )
                    Spacer(modifier = Modifier.weight(1f))
                }
                    },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            confirmButton = {
                PicTags(
                    search = search,
                    saveStateSnapshot = saveStateSnapshot,
                    getCollection = getCollection,
                    appState = appState,
                    state = state,
                    goToPicsListScreen = goToPicsListScreen,
                    goToAuthScreen = goToAuthScreen
                )
            }
        )
    }
    
    if (state.picIndex != null && state.pic != null) { // TODO
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 32.dp, end = 16.dp)
                    .then(
                        if (!windowInfo().isPortraitOrientation) Modifier.width(picDetailsWidth) else Modifier
                    )
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "${state.picIndex + 1} / ${state.picsList.size}")
                    Text(
                        text = state.pic.description,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                if (!windowInfo().isPortraitOrientation) {
                    IconButton(
                        onClick = {
                            showTags = true
                            blurContent(true)
                                  },
                        modifier = Modifier.offset(4.dp, 0.dp)
                    ) {
                        Icon(painterResource(R.drawable.tag_black_24dp), "show tags")
                    }
                }

                Box {
                    CollectionsDropDownMenu(
                        userCollections = appState.userCollections,
                        picCollections = appState.picCollections,
                        collectionToSaveTo = appState.collectionToSaveTo,
                        picId = state.pic.id,
                        editCollection = editCollection,
                        updateCollectionToSaveTo = updateCollectionToSaveTo,
                        blurContent = blurContent,
                        goToAuthScreen = goToAuthScreen
                    )
                }

                IconButton(
                    onClick = {
                        editCollection(
                            appState.collectionToSaveTo,
                            state.pic.id,
                            goToAuthScreen
                        )
                    },
                ) {
                    val collection = appState.collectionToSaveTo
                    val picInCollection = appState.picCollections.contains(collection)
                    Log.d(TAG, "PicDetails: collectionToSaveTo = $collection, picCollections: ${appState.picCollections}")
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