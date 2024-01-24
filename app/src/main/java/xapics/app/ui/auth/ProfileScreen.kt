package xapics.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import xapics.app.MainViewModel
import xapics.app.R
import xapics.app.Thumb
import xapics.app.ui.RollCard
import xapics.app.ui.theme.FilmTag
import xapics.app.ui.theme.PicBG

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    userCollections: List<Thumb>?,
    goToAuthScreen: () -> Unit,
    goToEditFilmsScreen: () -> Unit,
    goToUploadScreen: () -> Unit,
    goToPicsListScreen: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (viewModel.authState.userId) {
//            null -> goToAuthScreen() // TODO
            1 -> AdminView(viewModel, goToEditFilmsScreen, goToUploadScreen)
            else -> UserView(viewModel.authState.userId, userCollections, viewModel::getCollection, goToPicsListScreen, viewModel::renameOrDeleteCollection)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserView(
    userId: Int?,
    userCollections: List<Thumb>?,
    getCollection: (String) -> Unit,
    goToPicsListScreen: () -> Unit,
    renameOrDeleteCollection:(String, String?) -> Unit
) {
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var renamedTitle by remember { mutableStateOf("") }
    var collectionTitle by remember { mutableStateOf("") }
//    val focusRequester = remember { FocusRequester() }

    @Composable
    fun DeleteDialog() {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            modifier = Modifier
                .background(PicBG, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp)
                .padding(vertical = 36.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Delete \"$collectionTitle\"?")
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(onClick = { showDeleteDialog = false }) {
                        Text(text = "Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FilmTag
                        ),
                        onClick = {
                            renameOrDeleteCollection(collectionTitle, null)
                            showDeleteDialog = false
                            showRenameDialog = false
                        }
                    ) {
                        Text(text = "Delete")
                    }
                }
            }
        }
    }

    Text("User ID = $userId")
    LazyVerticalGrid(
        columns = GridCells.FixedSize(176.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        if(userCollections != null) {
            items(userCollections.size) {
                val rollTitle = userCollections[it].title
                val thumbUrl = userCollections[it].thumbUrl

                Box {
                    RollCard (width = 150.dp, isLoading = false, imageUrl = thumbUrl, rollTitle = rollTitle) {
                        getCollection(rollTitle)
                        goToPicsListScreen()
                    }
                    val modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(-(13).dp, 13.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .alpha(0.7f)
                        .background(Color(0x44000000))
                    if (rollTitle == "Favourites") {
                        IconButton (
                            modifier = modifier,
                            onClick = {
                                collectionTitle = "Favourites"
                                showDeleteDialog = true
                            }
                        ) {
                            Icon (painterResource(R.drawable.baseline_delete_outline_24),"Delete collection")
                        }
                    } else {
                        IconButton (
                            modifier = modifier,
                            onClick = {
                                renamedTitle = rollTitle
                                collectionTitle = rollTitle
                                showRenameDialog = true
                            }
                        ) {
                            Icon (painterResource(R.drawable.baseline_edit_24),"Rename or delete collection")
                        }
                    }
                }
            }
        }
    }
    if (showRenameDialog) {
        AlertDialog(onDismissRequest = { showRenameDialog = false }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = renamedTitle,
                    onValueChange = {renamedTitle = it},
                    singleLine = true,
                    label = { Text(text = "Rename collection") },
                    trailingIcon = {
                        IconButton(onClick = {
                            renameOrDeleteCollection(collectionTitle, renamedTitle)
                            showRenameDialog = false
                        }) {
                            Icon(painterResource(R.drawable.baseline_send_24), "Rename collection to $renamedTitle")
                        }
                                   },
                    keyboardActions = KeyboardActions(onAny = {
                        renameOrDeleteCollection(collectionTitle, renamedTitle)
                        showRenameDialog = false
                    }),
                    modifier = Modifier
                        .background(Color(0x55000000))
//                        .focusRequester(focusRequester),
                )
                Text(text = "or")
                Button(
                    onClick = {
                        showDeleteDialog = true
                    }
                ) {
                    Text(text = "Delete collection")
                    Icon(painterResource(R.drawable.baseline_delete_outline_24), contentDescription = null)
                }
            }
//            LaunchedEffect(Unit) {
//                focusRequester.requestFocus()
//            }
        }
    }
    if (showDeleteDialog) DeleteDialog()
}


@Composable
fun AdminView(viewModel: MainViewModel, goToEditFilmsScreen: () -> Unit, goToUploadScreen: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Button(
            onClick = {
                viewModel.getFilmsList()
                goToEditFilmsScreen()
            },
        ) {
            Text("Edit films")
        }
        Button(
            onClick = {
//                viewModel.getFilmsList()
                viewModel.getRollsList()
                goToUploadScreen()
            },
        ) {
            Text("Edit rolls / upload photos")
        }
    }
}