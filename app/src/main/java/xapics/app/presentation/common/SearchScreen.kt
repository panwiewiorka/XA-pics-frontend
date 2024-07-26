package xapics.app.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xapics.app.TagState
import xapics.app.presentation.AppState
import xapics.app.presentation.MainViewModel
import xapics.app.presentation.composables.PicTag
import xapics.app.presentation.theme.myTextButtonColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    viewModel: MainViewModel, appState: AppState, goToPicsListScreen: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            TextButton(
                enabled = appState.tags.any { it.state == TagState.SELECTED },
                colors = myTextButtonColors(),
                onClick = {
                    val filters = appState.tags.filter { it.state == TagState.SELECTED }
                        .map { "${it.type} = ${it.value}" }
                        .toString().drop(1).dropLast(1)
                    viewModel.search(filters)
                    goToPicsListScreen()
                }
            ) {
                Text(text = "Show filtered pics", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            TextButton(
                enabled = appState.tags.any { it.state == TagState.SELECTED },
                colors = myTextButtonColors(),
                onClick = { viewModel.getAllTags() }
            ) {
                Text(text = "Reset filters", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        FlowRow(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            appState.tags.forEach {
                PicTag(it) {
                    viewModel.getFilteredTags(it)
                }
            }
        }
    }
}