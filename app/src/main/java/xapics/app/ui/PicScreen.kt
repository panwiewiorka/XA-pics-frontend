package xapics.app.ui

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import xapics.app.AppState
import xapics.app.MainViewModel
import xapics.app.R
import xapics.app.TAG
import xapics.app.Thumb
import xapics.app.ui.theme.CollectionTag
import xapics.app.ui.theme.DefaultTag
import xapics.app.ui.theme.FilmTag
import xapics.app.ui.theme.PicBG
import xapics.app.ui.theme.YearTag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PicScreen(
    viewModel: MainViewModel, appState: AppState, goToPicsListScreen: () -> Unit
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
                        .background(PicBG)
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
                    CollectionsMenu(appState.userCollections, appState.picCollections, picId, viewModel::editCollection, viewModel::updateCollectionToSaveTo)
                }

                IconButton(
                    onClick = {
                        viewModel.editCollection(appState.collectionToSaveTo, picId)
                    },
                ) {
                    val collection = appState.collectionToSaveTo
                    val picInCollection = appState.picCollections.contains(collection)
                    if(collection == "Favourites") {
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
                viewModel.getPicsList(film = appState.pic?.film)
                goToPicsListScreen()
            }

            PicTag(appState.pic?.year?.toString() ?: "", YearTag) {
                viewModel.getPicsList(year = appState.pic?.year)
                goToPicsListScreen()
            }

            appState.picCollections.forEach {
                PicTag(text = it, color = CollectionTag) {
                    viewModel.getCollection(it)
                    goToPicsListScreen()
                }
            }

            val tags = appState.pic?.tags?.split(',')
            tags?.forEach {
                val tag = it.trim()
                PicTag(tag, DefaultTag) {
                    viewModel.getPicsList(tag = tag)
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
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsMenu(
    userCollections: List<Thumb>?,
    picCollections: List<String>,
    picId: Int,
    editCollection: (String, Int) -> Unit,
    updateCollectionToSaveTo:(String) -> Unit,
) {
    var collectionListOpened by remember { mutableStateOf(false) }
    var newCollectionDialogOpened by remember { mutableStateOf(false) }
    var newCollectionTitle by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    fun addToNewCollection(title: String) {
        editCollection(title, picId)
        updateCollectionToSaveTo(title)
        collectionListOpened = false
    }

    Column(
        horizontalAlignment = Alignment.End,
    ) {
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
                        Text("Favourites")
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = { editCollection("Favourites", picId) },
//                        modifier = Modifier.padding(end = 0.dp)
                    ) {
                        if (picCollections.contains("Favourites")) {
                            Icon(Icons.Filled.Favorite, "Remove from Favourites")
                        } else {
                            Icon(Icons.Outlined.FavoriteBorder, "Add to Favourites")
                        }
                    }
                },
                onClick = { addToNewCollection("Favourites") },
                modifier = Modifier.align(Alignment.End)
            )
            userCollections?.forEach {
                val collectionTitle = it.title
                if (collectionTitle != "Favourites") {
                    DropdownMenuItem(
                        text = { Text(collectionTitle) },
                        trailingIcon = {
                            IconButton(onClick = { editCollection(collectionTitle, picId) }) {
                                if (picCollections.contains(collectionTitle)) {
                                    Icon(Icons.Filled.Star, "Remove to $collectionTitle")
                                } else {
                                    Log.d(TAG, "CollectionsMenu: ${userCollections.size}, $collectionTitle, $picCollections, ${picCollections.contains(collectionTitle)}")
                                    Icon(painterResource(id = R.drawable.star_border), "Add to $collectionTitle")
                                }
                            }
                        },
                        onClick = { addToNewCollection(collectionTitle) },
                        modifier = Modifier.align(Alignment.End)
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
        AlertDialog(
            onDismissRequest = {
                newCollectionDialogOpened = false
                collectionListOpened = false
            }
        ) {
            OutlinedTextField(
                value = newCollectionTitle,
                onValueChange = {newCollectionTitle = it},
                singleLine = true,
                label = { Text(text = "Collection title") },
                trailingIcon = {
                    IconButton(onClick = {
                        addToNewCollection(newCollectionTitle)
                        newCollectionDialogOpened = false
                        collectionListOpened = false
                    }) {
                        Icon(painterResource(R.drawable.star_border), "Add to $newCollectionTitle collection")
                    }
                               },
//                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                keyboardActions = KeyboardActions(onAny = {
                    addToNewCollection(newCollectionTitle)
                    newCollectionDialogOpened = false
                }),
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