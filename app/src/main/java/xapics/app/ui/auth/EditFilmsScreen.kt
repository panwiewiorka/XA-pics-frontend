package xapics.app.ui.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import xapics.app.AppState
import xapics.app.Film
import xapics.app.FilmType
import xapics.app.FilmType.BW
import xapics.app.FilmType.NEGATIVE
import xapics.app.FilmType.SLIDE
import xapics.app.MainViewModel
import xapics.app.ShowHide


@Composable
fun EditFilmsScreen(
    viewModel: MainViewModel,
    appState: AppState,
    snackbarHostState: SnackbarHostState
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    if(appState.filmsList == null) {
        viewModel.updateFilmsListState(emptyList())
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
            .padding(horizontal = 32.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) { focusManager.clearFocus() }
    ) {

        FilmSelector(
            selectFilmToEdit = viewModel::selectFilmToEdit,
            filmsList = appState.filmsList,
            onRollsPage = false,
            shouldOpenMenu = appState.filmToEdit == null,
            editRollField = viewModel::editRollField
        )

        appState.filmToEdit?.let {
            OutlinedTextField(
                value = appState.filmToEdit.filmName,
                onValueChange = { viewModel.editFilmField(filmName = it) },
                label = { Text("Film name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequester.requestFocus() }),
            )

            OutlinedTextField(
                value = appState.filmToEdit.iso?.toString() ?: "",
                onValueChange = { viewModel.editFilmField(iso = it.toInt()) },
                label = { Text("ISO") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()},),
                modifier = Modifier.focusRequester(focusRequester)
                )

            RadioButtons(appState.filmToEdit, viewModel::editFilmField, focusManager::clearFocus)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.align(Alignment.End).padding(end = 16.dp),
            ) {
                Button(
                    enabled = !appState.isLoading,
                    onClick = {
                        val tempFilmsList = appState.filmsList?.toMutableList() ?: mutableListOf()
                        val filmIndex = tempFilmsList.indexOfFirst {
                            it.filmName == appState.filmToEdit.filmName
                        }

                        val savingFilmsList = if (filmIndex == -1) {
                            tempFilmsList.plus (appState.filmToEdit)
                        } else {
                            tempFilmsList[filmIndex] = appState.filmToEdit
                            tempFilmsList
                        }

                        viewModel.updateFilmsListState(savingFilmsList)
                        viewModel.postFilm(filmIndex == -1, appState.filmToEdit)

//                    CoroutineScope(Dispatchers.Default).launch {
//                        snackbarHostState.showSnackbar(message = "${it.filmName} saved", withDismissAction = true, duration = SnackbarDuration.Short)
//                    }
                    }
                ) {
                    Text("Save ${it.filmName}")
                }

                if (appState.isLoading) CircularProgressIndicator()

                if (appState.connectionError.isShown) {
                    Toast.makeText(
                        context,
                        "Error",
                        Toast.LENGTH_SHORT
                    ).show()

                    viewModel.showConnectionError(ShowHide.HIDE)
                }
            }
        }
    }
}

@Composable
fun FilmSelector(
    selectFilmToEdit: (Film?) -> Unit,
    filmsList: List<Film>?,
    onRollsPage: Boolean,
    shouldOpenMenu: Boolean,
    editRollField: (String?, String?, Boolean?) -> Unit,
    goToEditFilmsScreen: () -> Unit? = {}
) {
    var menuOpened by remember { mutableStateOf(!onRollsPage && shouldOpenMenu) }
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