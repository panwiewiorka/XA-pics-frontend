package xapics.app.ui.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xapics.app.AppState
import xapics.app.MainViewModel
import xapics.app.Roll
import xapics.app.ShowHide
import xapics.app.capitalize
import xapics.app.ui.composables.AsyncPic


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditRollsScreen(
    viewModel: MainViewModel,
    appState: AppState,
    goToAuthScreen: () -> Unit,
    goToEditFilmsScreen: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    if(appState.rollsList == null) {
        viewModel.updateRollsListState(emptyList())
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) { focusManager.clearFocus() }
    ) {
        RollSelector(
            shouldOpenMenu = appState.rollToEdit == null,
            onRollsPage = true,
            selectRollToEdit = viewModel::selectRollToEdit,
            search = viewModel::search,
            clearPicsList = viewModel::clearPicsList,
            rollsList = appState.rollsList
        )

        appState.rollToEdit?.let {
            OutlinedTextField(
                value = appState.rollToEdit.title,
                onValueChange = { viewModel.editRollField(title = it.capitalize()) },
                label = { Text("Roll name") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()}),
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .padding(top = 16.dp)
            ) {
                FilmSelector(
                    selectFilmToEdit = viewModel::selectFilmToEdit,
                    filmsList = appState.filmsList,
                    onRollsPage = true,
                    shouldOpenMenu = false,
                    editRollField = viewModel::editRollField,
                    goToEditFilmsScreen = goToEditFilmsScreen
                )

                Text(appState.rollToEdit.film, fontSize = 18.sp)
            }

            if (appState.rollToEdit.film.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .padding(horizontal = 32.dp)
                ) {
                    TextAndSwitch("Expired", appState.rollToEdit.expired, focusManager::clearFocus) { viewModel.editRollField(expired = !appState.rollToEdit.expired) }

                    TextAndSwitch("Cross-process", appState.rollToEdit.xpro, focusManager::clearFocus) { viewModel.editRollField(xpro = !appState.rollToEdit.xpro) }
                }

                if (!appState.picsList.isNullOrEmpty()) {
                    BoxWithConstraints {
                        val width = maxWidth
                        FlowRow(
                            modifier = Modifier.padding(vertical = 16.dp)
                        ) {
                            appState.picsList.forEach {
                                AsyncPic(
                                    url = it.imageUrl,
                                    description = it.description,
                                    modifier = Modifier
                                        .width(width / 4)
                                        .height(width / 6)
                                )
                            }
                        }
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.align(Alignment.End).padding(horizontal = 32.dp)
                ) {
                    Button(
                        enabled = !appState.isLoading && appState.rollToEdit.title.isNotEmpty(),
                        onClick = {
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

                            viewModel.updateRollsListState(savingRollsList ?: emptyList())
                            viewModel.postRoll(rollIndex == -1, appState.rollToEdit, goToAuthScreen)
                        }) {
                        Text("Save roll")
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
}

@Composable
fun RollSelector(
    shouldOpenMenu: Boolean,
    onRollsPage: Boolean,
    selectRollToEdit: (Roll?) -> Unit,
    search: (String) -> Unit,
    clearPicsList: () -> Unit,
    rollsList: List<Roll>?
) {
    var menuOpened by remember { mutableStateOf(shouldOpenMenu) }
    Row(
        modifier = Modifier.padding(start = 32.dp)
    ) {
        DropdownMenu(
            expanded = menuOpened,
            onDismissRequest = { menuOpened = false }
        ) {
            if (onRollsPage) {
                DropdownMenuItem(
                    text = { Text("[Add new roll]") },
                    onClick = {
                        clearPicsList()
                        selectRollToEdit(Roll())
                        menuOpened = false
                    }
                )
            }
            rollsList?.forEach {
                DropdownMenuItem(
                    text = { Text(it.title) },
                    onClick = {
                        selectRollToEdit(it)
                        search("roll = ${it.title}")
                        menuOpened = false
                    }
                )
            }
        }
        Button(onClick = { menuOpened = true }) {
            Text("Choose roll")
        }
    }
}

@Composable
fun TextAndSwitch(
    text: String,
    switchedOn: Boolean,
    onSwitch: (Boolean) -> Unit,
    clearFocus: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 36.dp)
    ) {
        Text(text = text)
        Switch(
            checked = switchedOn,
            onCheckedChange = {
                onSwitch(switchedOn)
                clearFocus()
                              },
            colors = SwitchDefaults.colors(
                checkedBorderColor = Color.Transparent,
                uncheckedBorderColor = Color.Transparent,
            )
//            modifier = Modifier.width(6.dp).height(6.dp)
        )
    }
}