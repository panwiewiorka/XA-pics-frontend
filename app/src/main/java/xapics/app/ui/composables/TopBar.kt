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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xapics.app.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopBar(
    popBackStack: () -> Unit,
    goToAuthScreen: () -> Unit,
    goToAdminScreen: () -> Unit,
    goToProfileScreen: () -> Unit,
    goToPicsListScreen: () -> Unit,
    goToSearchScreen: () -> Unit,
    updateTopBarCaption: (String) -> Unit,
    search: (String) -> Unit,
    showSearch: Boolean,
    changeShowSearchState: () -> Unit,
    logOut: () -> Unit,
    topBarCaption: String,
    page: String?,
    previousPage: String?,
    @StringRes pageName: Int,
    userName: String?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val focusRequester = remember { FocusRequester() }
        val text = when (page) {
            "PicsListScreen" -> topBarCaption
            "PicScreen" -> topBarCaption
            "AuthScreen" -> topBarCaption
            "ProfileScreen" -> userName ?: ""
            else -> stringResource(id = pageName)
        }
        var showClearSearchButton by rememberSaveable { mutableStateOf(false) }
        val searchText = ""
        var query by remember { mutableStateOf( TextFieldValue (
            text = searchText,
            selection = TextRange(searchText.length)
        )
        ) }

        @Composable
        fun HomeOrBackButton() {
            if (page == "HomeScreen") {
                IconButton(enabled = false, onClick = {  }) {
                    Image(painterResource(R.drawable.xa_pics_closed), contentDescription = null, modifier = Modifier.padding(6.dp))
                }
            } else {
                IconButton(onClick = { popBackStack() }) {
                    Icon(Icons.Outlined.ArrowBack, "go Back")
                }
            }
        }

        @Composable
        fun SearchField() {
            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = query,
                    onValueChange = {
                        showClearSearchButton = false
                        query = it
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (query.text != "") {
                            search(query.text)
                            goToPicsListScreen()
                        }
                        changeShowSearchState()
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .focusRequester(focusRequester),
                )
                Row(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterEnd)
                ) {
                    if (showClearSearchButton && query.text.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "clear search",
                            modifier = Modifier
//                            .padding(horizontal = 4.dp)
//                            .align(Alignment.CenterEnd)
                                .clickable {
                                    query = TextFieldValue("")
                                    showClearSearchButton = false
                                }
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Go to advanced search page",
                        modifier = Modifier
//                            .padding(horizontal = 4.dp)
//                            .align(Alignment.CenterEnd)
                            .clickable {
                                goToSearchScreen()
                            }
                    )
                }
            }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                showClearSearchButton = true
            }
        }

        @Composable
        fun TitleAndSearch() {
            if (showSearch) {
                SearchField()
            } else {
                Text(
                    text = text,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = Ellipsis,
                    modifier = Modifier
                        .basicMarquee()
                        .weight(1f)
                        .clickable(enabled = page == "PicScreen") { goToPicsListScreen() }
                )
            }

            IconButton(onClick = {
                if (showSearch && query.text != "") {
                    if(page == "SearchScreen") {
                        search(query.text)
                    } else {
                        search(query.text)
                    }
                    goToPicsListScreen()
                }
                changeShowSearchState()
            }) {
                Icon(Icons.Default.Search, "Search photos")
            }
        }

        @Composable
        fun ProfileOrLogOutButton() {
            if(page == "AdminScreen" || page == "ProfileScreen") {
                IconButton(onClick = {
                    popBackStack()
                    logOut() // TODO when logOut() finished -> goToAuthScreen()
                    goToAuthScreen()
                }) {
                    Icon(painterResource(id = R.drawable.baseline_logout_24), "Log out")
                }
            } else {
                IconButton(
                    enabled = page != "AuthScreen",
                    onClick = {
                        when (userName) {
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

        Spacer(modifier = Modifier.width(12.dp))

        TitleAndSearch()

        Spacer(modifier = Modifier.width(6.dp))

        ProfileOrLogOutButton()
    }
    LaunchedEffect(page) {
        if(showSearch) changeShowSearchState()
    }
}