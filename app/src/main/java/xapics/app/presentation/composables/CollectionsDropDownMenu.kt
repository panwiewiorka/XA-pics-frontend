package xapics.app.presentation.composables

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import xapics.app.R
import xapics.app.TAG
import xapics.app.Thumb
import xapics.app.presentation.theme.myTextFieldColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsDropDownMenu(
    userCollections: List<Thumb>?,
    picCollections: List<String>,
    collectionToSaveTo: String,
    picId: Int,
    editCollection: (String, Int, () -> Unit) -> Unit,
    updateCollectionToSaveTo:(String) -> Unit,
    blurContent:(Boolean) -> Unit,
    goToAuthScreen: () -> Unit,
) {
    var collectionListOpened by rememberSaveable { mutableStateOf(false) }
    var newCollectionDialogOpened by rememberSaveable { mutableStateOf(false) }
    var titleField by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    fun editCollectionAndCloseMenu(title: String) {
        editCollection(title, picId, goToAuthScreen)
        updateCollectionToSaveTo(title)
        collectionListOpened = false
    }

    Column(
        horizontalAlignment = Alignment.End,
    ) {
        val favs = stringResource(R.string.fav_collection)
        IconButton(onClick = { collectionListOpened = true },) {
            Icon(Icons.Outlined.Menu, "Add to another collection")
        }
        DropdownMenu(
            expanded = collectionListOpened,
            onDismissRequest = { collectionListOpened = false },
            offset = DpOffset((-12).dp, 0.dp)
        ) {
            DropdownMenuItem(
                text = {
                    Box {
                        Text(favs)
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = { editCollection(favs, picId, goToAuthScreen) },
                    ) {
                        if (picCollections.contains(favs)) {
                            Icon(Icons.Filled.Favorite, "Remove from $favs")
                        } else {
                            Icon(Icons.Outlined.FavoriteBorder, "Add to $favs")
                        }
                    }
                },
                onClick = { editCollectionAndCloseMenu(favs) },
                modifier = Modifier
                    .align(Alignment.End)
                    .then(if (collectionToSaveTo == favs) Modifier.background(MaterialTheme.colorScheme.secondary) else Modifier)
            )
            userCollections?.forEach {
                val collectionTitle = it.title
                if (collectionTitle != favs) {
                    DropdownMenuItem(
                        text = { Text(collectionTitle) },
                        trailingIcon = {
                            IconButton(onClick = { editCollection(collectionTitle, picId, goToAuthScreen) }) {
                                if (picCollections.contains(collectionTitle)) {
                                    Icon(Icons.Filled.Star, "Remove from $collectionTitle")
                                } else {
                                    Log.d(TAG, "CollectionsMenu: ${userCollections.size}, $collectionTitle, $picCollections, ${picCollections.contains(collectionTitle)}")
                                    Icon(painterResource(id = R.drawable.star_border), "Add to $collectionTitle")
                                }
                            }
                        },
                        onClick = { editCollectionAndCloseMenu(collectionTitle) },
                        modifier = Modifier
                            .align(Alignment.End)
                            .then(
                                if (collectionToSaveTo == collectionTitle) Modifier.background(
                                    MaterialTheme.colorScheme.secondary
                                ) else Modifier
                            )
                    )
                }
            }
            DropdownMenuItem(
                text = {
                    Box {
                        Text("New collection")
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { newCollectionDialogOpened = true }) {
                        Icon(Icons.Outlined.Add, "Add to new collection")
                    }
                },
                onClick = { newCollectionDialogOpened = true },
                modifier = Modifier.align(Alignment.End)
            )
        }
    }

    if (newCollectionDialogOpened) {
        blurContent(true)
        titleField = ""
        BasicAlertDialog(
            onDismissRequest = {
                newCollectionDialogOpened = false
                collectionListOpened = false
                blurContent(false)
            }
        ) {
            fun onNaming(newTitle: String) = when {
                newTitle == "" -> {
                    Toast.makeText(
                        context,
                        "Empty name is not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                userCollections?.firstOrNull { it.title.lowercase() == newTitle.lowercase() } != null -> {
                    Toast.makeText(
                        context,
                        "Collection already exists",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    editCollectionAndCloseMenu(newTitle)
                    newCollectionDialogOpened = false
                    blurContent(false)
                }
            }

            OutlinedTextField(
                value = titleField,
                onValueChange = {titleField = it},
                singleLine = true,
                label = { Text("Collection title") },
                trailingIcon = {
                    IconButton(onClick = { onNaming(titleField.trim()) }) {
                        Icon(painterResource(R.drawable.star_border), "Add to $titleField collection")
                    }
                },
                keyboardActions = KeyboardActions(onAny = { onNaming(titleField.trim()) }),
                colors = myTextFieldColors(),
                modifier = Modifier.focusRequester(focusRequester)
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}