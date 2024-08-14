package xapics.app.presentation.screens.search

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import xapics.app.Tag
import xapics.app.TagState
import xapics.app.presentation.components.ConnectionErrorButton
import xapics.app.presentation.components.PicTag
import xapics.app.presentation.theme.myTextButtonColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    getAllTags: () -> Unit,
    getFilteredTags: (Tag) -> Unit,
    tags: List<Tag>,
    messages: Flow<String>,
    isLoading: Boolean,
    connectionError: Boolean,
    showConnectionError: (Boolean) -> Unit,
    goToPicsListScreen: (String) -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(messages) {
        messages.collect { message ->
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    when {
        connectionError -> {
            ConnectionErrorButton {
                getAllTags()
                showConnectionError(false)
            }
        }

        isLoading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) { CircularProgressIndicator() }
        }

        else -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    TextButton(
                        enabled = tags.any { it.state == TagState.SELECTED },
                        colors = myTextButtonColors(),
                        onClick = {
                            val filters = tags.filter { it.state == TagState.SELECTED }
                                .map { "${it.type} = ${it.value}" }
                                .toString().drop(1).dropLast(1)
                            goToPicsListScreen(filters)
                        }
                    ) {
                        Text(text = "Show filtered pics", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    TextButton(
                        enabled = tags.any { it.state == TagState.SELECTED },
                        colors = myTextButtonColors(),
                        onClick = { getAllTags() }
                    ) {
                        Text(text = "Reset filters", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                FlowRow(
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    tags.forEach {
                        PicTag(it) {
                            getFilteredTags(it)
                        }
                    }
                }
            }
        }
    }
}