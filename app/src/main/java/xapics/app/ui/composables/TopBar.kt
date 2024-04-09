package xapics.app.ui.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import xapics.app.R
import xapics.app.ShowHide.HIDE
import xapics.app.ShowHide.SHOW
import xapics.app.TagState
import xapics.app.ui.AppState
import xapics.app.ui.MainViewModel
import xapics.app.ui.common.nonScaledSp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopBar(
    viewModel: MainViewModel = hiltViewModel(),
    appState: AppState,
    popBackStack: () -> Unit,
    goToAuthScreen: () -> Unit,
    goToAdminScreen: () -> Unit,
    goToProfileScreen: () -> Unit,
    goToPicsListScreen: () -> Unit,
    goToSearchScreen: () -> Unit,
    page: String?,
    previousPage: String?,
    @StringRes pageName: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val focusRequester = remember { FocusRequester() }
        val text = when (page) {
            "PicsListScreen" -> appState.topBarCaption
            "PicScreen" -> appState.topBarCaption
            "AuthScreen" -> appState.topBarCaption
            "ProfileScreen" -> appState.userName ?: ""
            else -> stringResource(id = pageName)
        }
        val searchText = ""
        var query by remember { mutableStateOf( TextFieldValue (
            text = searchText,
            selection = TextRange(searchText.length)
        )
        ) }

        fun filteredSearch() {
            val formattedQuery = query.text
                .replace(',', ' ')
                .replace('=', ' ')

            val filters = appState.tags.filter { it.state == TagState.SELECTED }
                .map { "${it.type} = ${it.value}" }
                .toString().drop(1).dropLast(1)

            when {
                page == "SearchScreen" && filters.isNotBlank() -> {
                    viewModel.search(
                        (if (formattedQuery.isBlank()) ""
                        else "search = $formattedQuery, ")
                                + filters
                    )
                    goToPicsListScreen()
                }
                formattedQuery.isNotBlank() -> {
                    viewModel.search("search = $formattedQuery")
                    if (page != "PicsListScreen") goToPicsListScreen()
                }
                else -> {}
            }
            viewModel.showSearch(HIDE)
        }

        @Composable
        fun HomeOrBackButton() {
            if (page == "HomeScreen") {
                IconButton(enabled = false, onClick = {  }) {
                    Image(painterResource(R.drawable.xa_pics_closed), contentDescription = null, modifier = Modifier.padding(6.dp))
                }
            } else {
                IconButton(enabled = true, onClick = {
                    when(page) {
                        "PicScreen" -> viewModel.loadStateSnapshot()
                        "PicsListScreen" -> {
                            viewModel.loadStateSnapshot()
                            viewModel.showPicsList(HIDE)
                        }
                        "AdminScreen" -> {
                            viewModel.getRollsList()
                            viewModel.getAllTags()
                        }
                    }
                    popBackStack()
//                    if (page == "PicsListScreen" || page == "PicScreen") loadStateSnapshot()
//                    popBackStack()
//                    if (page == "PicsListScreen") showPicsList(HIDE)
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
                keyboardActions = KeyboardActions(onSearch = { filteredSearch() }),
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
                    if (appState.searchField.isShown) {
                        SearchField()
                    } else {
                        Text(
                            text = text,
                            fontSize = 18.nonScaledSp,
                            maxLines = 1,
                            overflow = Ellipsis,
                            modifier = Modifier
                                .basicMarquee()
                                .weight(1f)
                                .clickable(enabled = page == "PicScreen") { goToPicsListScreen() }
                        )
                    }

                    if (page != "SearchScreen" && appState.searchField.isShown) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Go to advanced search page",
                            modifier = Modifier.clickable { goToSearchScreen() }
                        )
                    }

                    IconButton(onClick = {
                        if (appState.searchField.isShown) filteredSearch() else viewModel.showSearch(SHOW)
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
                            if (appState.searchField.isShown) MaterialTheme.colorScheme.outline else Color.Transparent,
                            CircleShape
                        )
                ) {}
            }
        }

        @Composable
        fun ProfileOrLogOutButton() {
            if(page == "AdminScreen" || page == "ProfileScreen") {
                IconButton(onClick = {
                    popBackStack()
                    viewModel.logOut()
                    goToAuthScreen()
                }) {
                    Icon(painterResource(id = R.drawable.baseline_logout_24), "Log out")
                }
            } else {
                IconButton(
                    enabled = page != "AuthScreen",
                    onClick = {
                        when (appState.userName) {
                            null -> goToAuthScreen()
                            "admin" -> goToAdminScreen()
                            else -> goToProfileScreen()
                        }
                    }
                ) {
                    Icon(Icons.Outlined.AccountCircle, "Go to Profile screen")
                }
            }
        }



        if (page == "ProfileScreen" && page == previousPage) popBackStack()

        HomeOrBackButton()

        Spacer(modifier = Modifier.width(6.dp))

        TitleAndSearch()

        ProfileOrLogOutButton()
    }
    LaunchedEffect(page) {
        if (appState.searchField.isShown) viewModel.showSearch(HIDE)
    }
}