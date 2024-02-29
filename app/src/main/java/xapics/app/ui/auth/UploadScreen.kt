package xapics.app.ui.auth

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xapics.app.AppState
import xapics.app.MainViewModel
import xapics.app.Roll
import xapics.app.TAG
import java.io.File
import java.io.InputStream


@Composable
fun UploadScreen(
    viewModel: MainViewModel,
    appState: AppState,
    goToHomeScreen: () -> Unit,
    goToEditFilmsScreen: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val focusManager = LocalFocusManager.current

    Log.d(TAG, "EditRollsScreen: ROLLS LIST = ${appState.rollsList}")

    if(appState.rollsList == null) {
        viewModel.updateRollsListState(emptyList())
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) { focusManager.clearFocus() }
    ) {
        Button(onClick = { goToHomeScreen() }) {
            Text("Go home!")
        }

        RollSelector(viewModel::selectRollToEdit, appState.rollsList)

        appState.rollToEdit?.let {
            OutlinedTextField(
                value = appState.rollToEdit.title,
                onValueChange = { viewModel.editRollField(title = it) },
                label = { Text("Roll name") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()}),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(appState.rollToEdit.film)

                FilmSelector(selectFilmToEdit = viewModel::selectFilmToEdit, filmsList = appState.filmsList, true, viewModel::editRollField, goToEditFilmsScreen)
            }

            TextAndSwitch("Expired", appState.rollToEdit.expired, focusManager::clearFocus) { viewModel.editRollField(expired = !appState.rollToEdit.expired) }

            TextAndSwitch("X-pro", appState.rollToEdit.xpro, focusManager::clearFocus) { viewModel.editRollField(xpro = !appState.rollToEdit.xpro) }

            TextAndSwitch("Filmed not on XA", appState.rollToEdit.nonXa, focusManager::clearFocus) { viewModel.editRollField(nonXa = !appState.rollToEdit.nonXa) }

            var imageUri by remember {
                mutableStateOf<Uri?>(null)
            }
            val context = LocalContext.current
            val myResolver = context.contentResolver

            fun File.copyInputStreamToFile(inputStream: InputStream) {
                this.outputStream().use { fileOut ->
                    inputStream.copyTo(fileOut)
                }
            }

            Button(onClick = {
                val myStream = imageUri?.let { myResolver.openInputStream(it) }
                val myFile = File.createTempFile("image.jpg", null, context.cacheDir)
                if (myStream != null) {
                    myFile.copyInputStreamToFile(myStream)
                    myStream.close()
                }
            viewModel.uploadImage(appState.rollToEdit.title, myFile)
            }) {
                Text("Upload image (test)")
            }

            val pickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(),
                onResult = { uri -> imageUri = uri }
            )
            Button(onClick = {
                val tempRollsList = appState.rollsList?.toMutableList()
                val rollIndex = tempRollsList?.indexOfFirst {
                    it.title == appState.rollToEdit.title
                }

                val savingRollsList = if (rollIndex == -1) {
                    tempRollsList.plus (appState.rollToEdit)
                } else {
                    tempRollsList?.set(rollIndex!!, appState.rollToEdit)
                    tempRollsList
                }

                viewModel.updateRollsListState(savingRollsList?: emptyList())
                viewModel.postRoll(rollIndex == -1, appState.rollToEdit)

                CoroutineScope(Dispatchers.Default).launch {
                    snackbarHostState.showSnackbar(message = "${it.title} saved", withDismissAction = true, duration = SnackbarDuration.Short)
                }

                pickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }) {
                Text("pick photo")
            }

            AsyncImage(model = imageUri, contentDescription = null)
        }
    }
}

@Composable
fun RollSelector(selectRollToEdit: (Roll?) -> Unit, rollsList: List<Roll>?) {
    var menuOpened by remember { mutableStateOf(false) }
    Row {
        DropdownMenu(
            expanded = menuOpened,
            onDismissRequest = { menuOpened = false }
        ) {
            val selectRollOnClick: (Roll?) -> Unit = {
                selectRollToEdit(it)
                menuOpened = false
            }
            DropdownMenuItem(
                text = { Text("[Add new roll]") },
                onClick = { selectRollOnClick(Roll()) }
            )
            if (rollsList != null) {
                repeat (rollsList.size) {
                    DropdownMenuItem(
                        text = { Text(rollsList[it].title) },
                        onClick = { selectRollOnClick(rollsList[it]) }
                    )
                }
            }
        }
        Button(onClick = { menuOpened = true }) {
            Text("Choose roll")
        }
    }
}