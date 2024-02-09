package xapics.app.ui.auth

import android.content.Context
import android.util.Log
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import xapics.app.MainViewModel
import xapics.app.R
import xapics.app.TAG
import xapics.app.Thumb
import xapics.app.auth.AuthResult
import xapics.app.ui.RollCard
import xapics.app.ui.theme.FilmTag
import xapics.app.ui.theme.PicBG

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    isLoading: Boolean,
    userName: String?,
    userCollections: List<Thumb>?,
    goToAuthScreen: () -> Unit,
    goToPicsListScreen: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel, context) {
        Log.d(TAG, "ProfileScreen: Launched Effect started")

        viewModel.authResults.collect { result ->
            Log.d(TAG, "ProfileScreen: result is $result")
            when(result) {
                is AuthResult.Authorized -> { }
                is AuthResult.Conflicted -> {
                    Toast.makeText(
                        context,
                        result.data.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is AuthResult.Unauthorized -> {
                    Log.d(TAG, "result is Unauthorized, goToAuthScreen()")
                    goToAuthScreen()
                }
                is AuthResult.UnknownError -> {
                    Toast.makeText(
                        context,
                        "An unknown error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (userName != null) viewModel.getUserInfo()
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        UserView(
            userCollections,
            viewModel::getCollection,
            goToPicsListScreen,
            viewModel::renameOrDeleteCollection,
            context
        )
        if (isLoading) CircularProgressIndicator()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserView(
    userCollections: List<Thumb>?,
    getCollection: (String, String) -> Unit,
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
            Text("Favourite pics will be show here")

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
                val currentPage = stringResource(R.string.profile_screen)

                Box {
                    RollCard(
                        width = 150.dp,
                        isLoading = false,
                        imageUrl = thumbUrl,
                        rollTitle = rollTitle
                    ) {
                        getCollection(currentPage, rollTitle)
                        goToPicsListScreen()
                    }
                    val modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(-(13).dp, 13.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .alpha(0.7f)
                        .background(Color(0x44000000))
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
                                "Delete collection"
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
                                "Rename or delete collection"
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
                    modifier = Modifier
                        .background(Color(0x55000000))
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
        }
    }
    if (showDeleteDialog) DeleteDialog()
}