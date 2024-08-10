package xapics.app.presentation.topBar

import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import xapics.app.R
import xapics.app.Screen
import xapics.app.TagState
import xapics.app.nonScaledSp
import xapics.app.presentation.AppState

@Composable
fun TopBar(
    showSearch: (Boolean) -> Unit,
    loadStateSnapshot: () -> Unit,
    logOut: () -> Unit,
    appState: AppState,
    caption: String,
    saveCaption: (String) -> Unit,
    goBack: () -> Unit,
    goToAuthScreen: () -> Unit,
    goToProfileScreen: () -> Unit,
    goToPicsListScreen: (query: String) -> Unit,
    goToSearchScreen: () -> Unit,
    page: String,
    previousPage: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val focusRequester = remember { FocusRequester() }

        val text =  caption
        val searchText = ""
        var query by remember { mutableStateOf (
            TextFieldValue (
                text = searchText,
                selection = TextRange(searchText.length)
            )
        ) }

        fun formattedSearch() {
            val formattedQuery = query.text
                .replace(',', ' ')
                .replace('=', ' ')

            val filters = appState.tags.filter { it.state == TagState.SELECTED }
                .map { "${it.type} = ${it.value}" }
                .toString().drop(1).dropLast(1)

            when {
                page == Screen.Search.toString() && filters.isNotBlank() -> {
                    val prefix = if (formattedQuery.isBlank()) "" else "search = $formattedQuery, "
                    goToPicsListScreen(prefix + filters)
                }
                formattedQuery.isNotBlank() -> goToPicsListScreen("search = $formattedQuery")
                else -> {}
            }
            showSearch(false)
        }

        @Composable
        fun HomeOrBackButton() {
            if (page == Screen.Home.toString()) {
                IconButton(enabled = false, onClick = {  }) {
                    Image(painterResource(R.drawable.xa_pics_closed), contentDescription = null, modifier = Modifier.padding(6.dp))
                }
            } else {
                IconButton(enabled = true, onClick = {
                    if (page == Screen.PicsList.NAME || page == Screen.Pic.NAME) loadStateSnapshot()
                    goBack()
                }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, "go Back")
                }
            }
        }

        @Composable
        fun SearchField() {
            BasicTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                textStyle = TextStyle(fontSize = 16.nonScaledSp, color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { formattedSearch() }),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                    .focusRequester(focusRequester),
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }

        @Composable
        fun TitleAndSearch() {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (appState.showSearch) {
                        SearchField()
                    } else {
                        Text(
                            text = text,
                            fontSize = 18.nonScaledSp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .basicMarquee()
                                .weight(1f)
                        )
                    }

                    if (page != Screen.Search.toString() && appState.showSearch) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Go to advanced search page",
                            modifier = Modifier.clickable { goToSearchScreen() } // << caption also updates inside of here
                        )
                    }

                    IconButton(onClick = {
                        if (appState.showSearch) formattedSearch() else showSearch(true)
                    }) {
                        Icon(Icons.Default.Search, "Search photos", modifier = Modifier.offset(0.dp, 1.dp))
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .border(
                            1.dp,
                            if (appState.showSearch) MaterialTheme.colorScheme.outline else Color.Transparent,
                            CircleShape
                        )
                ) {}
            }
        }

        @Composable
        fun ProfileOrLogOutButton() {
            if(page == Screen.Profile.toString()) {
                IconButton(onClick = {
                    logOut()
//                    saveCaption("Log in")
                    goToAuthScreen()
                }) {
                    Icon(painterResource(id = R.drawable.baseline_logout_24), "Log out")
                }
            } else {
                IconButton(
                    enabled = page != Screen.Auth.toString(),
                    onClick = {
                        when (appState.userName) {
                            null -> {
//                                saveCaption("Log in")
                                goToAuthScreen()
                            }
                            else -> {
//                                saveCaption(appState.userName)
                                goToProfileScreen()
                            }
                        }
                    }
                ) {
                    Icon(Icons.Outlined.AccountCircle, "Go to Profile screen")
                }
            }
        }



        if (page == Screen.Profile.toString() && page == previousPage) goBack() // todo check double caption record in DB

        HomeOrBackButton()

        Spacer(modifier = Modifier.width(6.dp))

        TitleAndSearch()

        ProfileOrLogOutButton()
    }
    LaunchedEffect(page) {
        if (appState.showSearch) showSearch(false)
    }
}