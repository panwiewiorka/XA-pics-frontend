package xapics.app.ui.composables

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import xapics.app.R
import xapics.app.TAG
import xapics.app.Thumb

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsDropDownMenu(
    userCollections: List<Thumb>?,
    picCollections: List<String>,
    collectionToSaveTo: String,
    picId: Int,
    editCollection: (String, Int) -> Boolean,
    updateCollectionToSaveTo:(String) -> Unit,
    rememberToGetBackAfterLoggingIn: (Boolean?) -> Unit,
    goToAuthScreen: () -> Unit,
) {
    var collectionListOpened by rememberSaveable { mutableStateOf(false) }
    var newCollectionDialogOpened by rememberSaveable { mutableStateOf(false) }
    var newCollectionTitle by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    fun addToNewCollectionOrLogIn(title: String) {
        val isAuthorized = editCollection(title, picId)
        collectionListOpened = false
        if(!isAuthorized) {
            rememberToGetBackAfterLoggingIn(true)
            goToAuthScreen()
        } else {
            updateCollectionToSaveTo(title)
        }
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
                        onClick = {
                            val isAuthorized = editCollection(favs, picId)
                            if(!isAuthorized) {
                                rememberToGetBackAfterLoggingIn(true)
                                goToAuthScreen()
                            }
                        },
                    ) {
                        if (picCollections.contains(favs)) {
                            Icon(Icons.Filled.Favorite, "Remove from $favs")
                        } else {
                            Icon(Icons.Outlined.FavoriteBorder, "Add to $favs")
                        }
                    }
                },
                onClick = { addToNewCollectionOrLogIn(favs) },
                modifier = Modifier
                    .align(Alignment.End)
                    .then(if (collectionToSaveTo == favs) Modifier.background(Color.DarkGray) else Modifier)
            )
            userCollections?.forEach {
                val collectionTitle = it.title
                if (collectionTitle != favs) {
                    DropdownMenuItem(
                        text = { Text(collectionTitle) },
                        trailingIcon = {
                            IconButton(onClick = {
                                val isAuthorized = editCollection(collectionTitle, picId)
                                if(!isAuthorized) {
                                    rememberToGetBackAfterLoggingIn(true)
                                    goToAuthScreen()
                                }
                            }) {
                                if (picCollections.contains(collectionTitle)) {
                                    Icon(Icons.Filled.Star, "Remove to $collectionTitle")
                                } else {
                                    Log.d(TAG, "CollectionsMenu: ${userCollections.size}, $collectionTitle, $picCollections, ${picCollections.contains(collectionTitle)}")
                                    Icon(painterResource(id = R.drawable.star_border), "Add to $collectionTitle")
                                }
                            }
                        },
                        onClick = { addToNewCollectionOrLogIn(collectionTitle) },
                        modifier = Modifier
                            .align(Alignment.End)
                            .then(
                                if (collectionToSaveTo == collectionTitle) Modifier.background(
                                    Color.DarkGray
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
        newCollectionTitle = ""
        AlertDialog(
            onDismissRequest = {
                newCollectionDialogOpened = false
                collectionListOpened = false
            }
        ) {
            fun onNaming() = when {
                newCollectionTitle == "" -> {
                    Toast.makeText(
                        context,
                        "Empty name is not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                newCollectionTitle == " " -> {
                    Toast.makeText(
                        context,
                        "Empty name is not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                userCollections?.firstOrNull { it.title == newCollectionTitle }?.title == newCollectionTitle -> {
                    Toast.makeText(
                        context,
                        "Collection already exists",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    addToNewCollectionOrLogIn(newCollectionTitle)
                    newCollectionDialogOpened = false
                }
            }

            OutlinedTextField(
                value = newCollectionTitle,
                onValueChange = {newCollectionTitle = it},
                singleLine = true,
                label = { Text(text = "Collection title") },
                trailingIcon = {
                    IconButton(onClick = { onNaming() }) {
                        Icon(painterResource(R.drawable.star_border), "Add to $newCollectionTitle collection")
                    }
                },
//                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                keyboardActions = KeyboardActions(onAny = { onNaming() }),
                modifier = Modifier
                    .background(Color(0x55000000))
                    .focusRequester(focusRequester),
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}