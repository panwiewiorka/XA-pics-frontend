package xapics.app.ui.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import xapics.app.MainViewModel
import xapics.app.R
import xapics.app.ShowHide.HIDE
import xapics.app.Thumb
import xapics.app.ui.composables.ConnectionErrorButton
import xapics.app.ui.composables.RollCard
import xapics.app.ui.theme.AlmostWhite
import xapics.app.ui.theme.AlphaBlack
import xapics.app.ui.theme.myTextFieldColors

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    isLoading: Boolean,
    userName: String?,
    userCollections: List<Thumb>?,
    connectionError: Boolean,
    goToAuthScreen: () -> Unit,
    goToPicsListScreen: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (userName != null) viewModel.getUserInfo(goToAuthScreen)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            connectionError -> {
                ConnectionErrorButton {
                    viewModel.showConnectionError(HIDE)
                    viewModel.getUserInfo(goToAuthScreen)
                }
            }
            isLoading -> {
                CircularProgressIndicator()
            }
            else -> {
                UserView(
                    userCollections,
                    viewModel::getCollection,
                    goToPicsListScreen,
                    viewModel::renameOrDeleteCollection,
                    context
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserView(
    userCollections: List<Thumb>?,
    getCollection: (String) -> Unit,
    goToPicsListScreen: () -> Unit,
    renameOrDeleteCollection:(String, String?) -> Unit,
    context: Context,
) {
    var showRenameDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var renamedTitle by rememberSaveable { mutableStateOf("") }
    var collectionTitle by rememberSaveable { mutableStateOf("") }

    @Composable
    fun DeleteDialog() {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp))
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
                            containerColor = MaterialTheme.colorScheme.error
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

    if(userCollections.isNullOrEmpty()) {
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
    } else {
        LazyVerticalGrid(
            columns = GridCells.FixedSize(176.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(userCollections.size) {
                val rollTitle = userCollections[it].title
                val thumbUrl = userCollections[it].thumbUrl
                val favs = stringResource(R.string.fav_collection)

                Box {
                    RollCard(
                        width = 150.dp,
                        isLoading = false,
                        imageUrl = thumbUrl,
                        rollTitle = rollTitle
                    ) {
                        getCollection(rollTitle)
                        goToPicsListScreen()
                    }
                    val modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(-(13).dp, 13.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .alpha(0.7f)
                        .background(AlphaBlack)
                    if (rollTitle == favs) {
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                collectionTitle = favs
                                showDeleteDialog = true
                            }
                        ) {
                            Icon(
                                painterResource(R.drawable.baseline_delete_outline_24),
                                "Delete collection",
                                tint = AlmostWhite
                            )
                        }
                    } else {
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                renamedTitle = rollTitle
                                collectionTitle = rollTitle
                                showRenameDialog = true
                            }
                        ) {
                            Icon(
                                painterResource(R.drawable.baseline_edit_24),
                                "Rename or delete collection",
                                tint = AlmostWhite
                            )
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
                fun onRename() = when {
                    renamedTitle == collectionTitle -> {
                        showRenameDialog = false
                    }
                    renamedTitle == "" -> {
                        Toast.makeText(
                            context,
                            "Empty name is not available",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    renamedTitle == " " -> {
                        Toast.makeText(
                            context,
                            "Empty name is not available",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    userCollections?.firstOrNull { it.title == renamedTitle } != null -> {
                        Toast.makeText(
                            context,
                            "Name is taken by another collection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        renameOrDeleteCollection(collectionTitle, renamedTitle)
                        showRenameDialog = false
                    }
                }

                OutlinedTextField(
                    value = renamedTitle,
                    onValueChange = {renamedTitle = it},
                    singleLine = true,
                    label = { Text(text = "Rename collection") },
                    trailingIcon = {
                        IconButton(onClick = { onRename() }) {
                            Icon(painterResource(R.drawable.baseline_send_24), "Rename collection to $renamedTitle")
                        }
                                   },
                    keyboardActions = KeyboardActions(onAny = { onRename() }),
                    colors = myTextFieldColors(),
                    modifier = Modifier
//                        .background(Color(0x55000000))
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
    if (showDeleteDialog) DeleteDialog()
}