package xapics.app.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xapics.app.AppState
import xapics.app.Film
import xapics.app.FilmType
import xapics.app.FilmType.*
import xapics.app.MainViewModel


@Composable
fun EditFilmsScreen(
    viewModel: MainViewModel,
    appState: AppState,
    goToHomeScreen: () -> Unit,
    goToUploadScreen: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val focusManager = LocalFocusManager.current
//    Log.d(TAG, appState.filmsList.toString())

    if(appState.filmsList == null) {
//        Log.d(TAG, "EditFilmsScreen: FILMS LIST = NULL")
        viewModel.updateFilmsListState(emptyList())
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

        FilmSelector(viewModel::selectFilmToEdit, appState.filmsList, false, viewModel::editRollField)

        appState.filmToEdit?.let {
            OutlinedTextField(
                value = appState.filmToEdit.filmName,
                onValueChange = { viewModel.editFilmField(filmName = it) },
                label = { Text("Film name") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()}),
            )

            OutlinedTextField(
                value = appState.filmToEdit.iso?.toString() ?: "",
                onValueChange = { viewModel.editFilmField(iso = it.toInt()) },
                label = { Text("ISO") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()},),
                )

            RadioButtons(appState.filmToEdit, viewModel::editFilmField, focusManager::clearFocus)

            Spacer(Modifier.weight(1f))

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    val tempFilmsList = appState.filmsList?.toMutableList()
                    val filmIndex = tempFilmsList?.indexOfFirst {
                        it.filmName == appState.filmToEdit.filmName
                    }

                    val savingFilmsList = if (filmIndex == -1) {
                        tempFilmsList.plus (appState.filmToEdit)
                    } else {
                        tempFilmsList?.set(filmIndex!!, appState.filmToEdit)
                        tempFilmsList
                    }

                    viewModel.updateFilmsListState(savingFilmsList?: emptyList())
                    viewModel.postFilm(filmIndex == -1, appState.filmToEdit)

                    CoroutineScope(Dispatchers.Default).launch {
                        snackbarHostState.showSnackbar(message = "${it.filmName} saved", withDismissAction = true, duration = SnackbarDuration.Short)
                    }
                }
            ) {
                Text("Save ${it.filmName}")
            }
        }
    }
}

@Composable
fun FilmSelector(
    selectFilmToEdit: (Film?) -> Unit,
    filmsList: List<Film>?,
    onRollsPage: Boolean,
    editRollField: (String?, String?, Boolean?) -> Unit,
    goToEditFilmsScreen: () -> Unit? = {}
) {
    var menuOpened by remember { mutableStateOf(false) }
    Row {
        DropdownMenu(
            expanded = menuOpened,
            onDismissRequest = { menuOpened = false }
        ) {
            val selectFilmOnClick: (Film?) -> Unit = {
                selectFilmToEdit(it)
                menuOpened = false
            }
            DropdownMenuItem(
                text = { Text("[Add new film]") },
                onClick = {
                    if (onRollsPage) {
                        goToEditFilmsScreen()
                    } else {
                        selectFilmOnClick(Film())
                    }
                }
            )
            if (filmsList != null) {
                repeat (filmsList.size) {
                    DropdownMenuItem(
                        text = { Text(filmsList[it].filmName) },
                        onClick = {
                            selectFilmOnClick(filmsList[it])
                            if (onRollsPage) editRollField(null, filmsList[it].filmName, null)
                        }
                    )
                }
            }
        }
        Button(onClick = { menuOpened = true }) {
            Text("Choose film")
        }
    }
}

@Composable
fun TextAndSwitch(text: String, switchedOn: Boolean, onSwitch: (Boolean) -> Unit, clearFocus: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = text)
        Switch(checked = switchedOn, onCheckedChange = {clearFocus(); onSwitch(switchedOn)})
    }
}

@Composable
fun RadioButtons(
    filmToEdit: Film,
    editFilmField: (String?, Int?, FilmType?) -> Unit,
    clearFocus: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val offsetModifier = Modifier.offset((-6).dp)
        Text(text = "Slide")
        RadioButton(selected = filmToEdit.type == SLIDE, onClick = { clearFocus(); editFilmField(null, null, SLIDE) }, modifier = offsetModifier)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "Negative")
        RadioButton(selected =  filmToEdit.type == NEGATIVE, onClick = { clearFocus(); editFilmField(null, null, NEGATIVE) }, modifier = offsetModifier)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "B/W")
        RadioButton(selected =  filmToEdit.type == BW, onClick = { clearFocus(); editFilmField(null, null, BW) }, modifier = offsetModifier)
    }
}