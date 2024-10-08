package xapics.app.presentation.screens.pic.components

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
import xapics.app.R
import xapics.app.presentation.components.CollectionsDropDownMenu
import xapics.app.presentation.screens.pic.PicScreenState
import xapics.app.presentation.windowInfo

@Composable
fun PicDetails(
    editCollection: (collection: String, picId: Int, onAuthError: () -> Unit) -> Unit,
    updateCollectionToSaveTo:(String) -> Unit,
    blurContent: (Boolean) -> Unit,
    picScreenState: PicScreenState,
    picDetailsWidth: Dp,
    goToAuthScreen: () -> Unit,
    goToPicsListScreen: (searchQuery: String) -> Unit,
) {
    var showTags by remember { mutableStateOf(false) }
    val pic = picScreenState.picsList[picScreenState.picIndex!!]
    
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
                        text = pic.description,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        )
                    Spacer(modifier = Modifier.weight(1f))
                }
                    },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            confirmButton = {
                PicTags(
                    picScreenState = picScreenState,
                    goToPicsListScreen = goToPicsListScreen
                )
            }
        )
    }
    
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
                Text(text = "${picScreenState.picIndex + 1} / ${picScreenState.picsList.size}")
                Text(
                    text = pic.description,
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
                    userCollections = picScreenState.userCollections,
                    picCollections = picScreenState.picCollections,
                    collectionToSaveTo = picScreenState.collectionToSaveTo,
                    picId = pic.id,
                    editCollection = editCollection,
                    updateCollectionToSaveTo = updateCollectionToSaveTo,
                    blurContent = blurContent,
                    goToAuthScreen = goToAuthScreen
                )
            }

            IconButton(
                onClick = {
                    editCollection(
                        picScreenState.collectionToSaveTo,
                        pic.id,
                        goToAuthScreen
                    )
                },
            ) {
                val collection = picScreenState.collectionToSaveTo
                val picInCollection = picScreenState.picCollections.contains(collection)
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