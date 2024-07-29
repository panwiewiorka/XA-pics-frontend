package xapics.app.presentation.auth.profileScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import xapics.app.R
import xapics.app.Thumb
import xapics.app.presentation.theme.AlmostWhite
import xapics.app.presentation.theme.myTextFieldColors
import xapics.app.presentation.windowInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserView(
    userCollections: List<Thumb>?,
    getCollection: (collection: String, onAuthError: () -> Unit) -> Unit,
    goToPicsListScreen: () -> Unit,
    goToAuthScreen: () -> Unit,
    renameOrDeleteCollection: (currentTitle: String, newTitle: String?, onAuthError: () -> Unit) -> Unit,
    context: Context,
) {
    var showRenameDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var renamedTitle by rememberSaveable { mutableStateOf("") }
    var collectionTitle by rememberSaveable { mutableStateOf("") }
    val blurAmount by animateDpAsState(
        targetValue = if (showDeleteDialog || showRenameDialog) 10.dp else 0.dp,
        label = "user collections blur"
    )
    val windowInfo = windowInfo()
    val isPortrait = windowInfo.isPortraitOrientation

    when {
        userCollections.isNullOrEmpty() -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Icon (Icons.Filled.Favorite, null)

                    Spacer(modifier = Modifier.width(20.dp))

                    Icon (Icons.Filled.Star, null)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Favourite pics will be shown here")
            }
        }
        isPortrait -> {
            LazyColumn(
                modifier = Modifier
                    .blur(blurAmount)
                    .widthIn(min = 100.dp, max = 200.dp)
            ) {
                items(userCollections.size) {
                    val rollTitle = userCollections[it].title
                    val thumbUrl = userCollections[it].thumbUrl
                    val favs = stringResource(R.string.fav_collection)

                    CollectionCard(
                        thumbUrl = thumbUrl,
                        rollTitle = rollTitle,
                        favs = favs,
                        isPortrait = true,
                        onClick = {
                            getCollection(rollTitle, goToAuthScreen)
                            goToPicsListScreen()
                        },
                        onRenameCollection = {
                            renamedTitle = rollTitle
                            collectionTitle = rollTitle
                            showRenameDialog = true
                        },
                        onDeleteFavourites = {
                            collectionTitle = favs
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
        else -> {
            LazyRow(
                modifier = Modifier
                    .blur(blurAmount)
                    .heightIn(min = 80.dp, max = 160.dp)
            ) {
                items(userCollections.size) {
                    val rollTitle = userCollections[it].title
                    val thumbUrl = userCollections[it].thumbUrl
                    val favs = stringResource(R.string.fav_collection)

                    CollectionCard(
                        thumbUrl = thumbUrl,
                        rollTitle = rollTitle,
                        favs = favs,
                        isPortrait = false,
                        onClick = {
                            getCollection(rollTitle, goToAuthScreen)
                            goToPicsListScreen()
                        },
                        onRenameCollection = {
                            renamedTitle = rollTitle
                            collectionTitle = rollTitle
                            showRenameDialog = true
                        },
                        onDeleteFavourites = {
                            collectionTitle = favs
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showRenameDialog) {
        BasicAlertDialog(
            onDismissRequest = { showRenameDialog = false },
            modifier = if (!showDeleteDialog) Modifier else Modifier.alpha(0f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                fun onRename() = when {
                    renamedTitle == collectionTitle -> { showRenameDialog = false }

                    renamedTitle == "" -> { Toast.makeText(context, "Empty name is not available", Toast.LENGTH_SHORT).show() }

                    renamedTitle == " " -> { Toast.makeText(context, "Empty name is not available", Toast.LENGTH_SHORT).show() }

                    userCollections?.firstOrNull { it.title == renamedTitle } != null -> { Toast.makeText(context, "Name is taken by another collection", Toast.LENGTH_SHORT).show() }

                    else -> {
                        renameOrDeleteCollection(collectionTitle, renamedTitle.trim(), goToAuthScreen)
                        showRenameDialog = false
                    }
                }

                OutlinedTextField(
                    value = renamedTitle,
                    onValueChange = { renamedTitle = it },
                    singleLine = true,
                    label = { Text(text = "Rename collection") },
                    trailingIcon = {
                        IconButton(onClick = { onRename() }) {
                            Icon(painterResource(R.drawable.baseline_send_24), "Rename collection to $renamedTitle")
                        }
                    },
                    keyboardActions = KeyboardActions(onAny = { onRename() }),
                    colors = myTextFieldColors(),
                )

                Text(text = "or", color = AlmostWhite)

                Button(
                    onClick = {
                        showDeleteDialog = true
                    }
                ) {
                    Text(text = "Delete collection")

                    Icon(painterResource(R.drawable.baseline_delete_outline_24), contentDescription = null)
                }
            }
        }
    }
    if (showDeleteDialog) DeleteDialog(
        text = "Delete \"$collectionTitle\"?",
        onDismiss = { showDeleteDialog = false }
    ) {
        renameOrDeleteCollection(collectionTitle, null, goToAuthScreen)
        showDeleteDialog = false
        showRenameDialog = false
    }
}