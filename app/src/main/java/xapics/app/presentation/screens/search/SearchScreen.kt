package xapics.app.presentation.screens.search

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
import xapics.app.Tag
import xapics.app.TagState
import xapics.app.presentation.components.PicTag
import xapics.app.presentation.theme.myTextButtonColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    getAllTags: () -> Unit,
    getFilteredTags: (Tag) -> Unit,
    tags: List<Tag>,
    goToPicsListScreen: (String) -> Unit,
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
                enabled = tags.any { it.state == TagState.SELECTED },
                colors = myTextButtonColors(),
                onClick = {
                    val filters = tags.filter { it.state == TagState.SELECTED }
                        .map { "${it.type} = ${it.value}" }
                        .toString().drop(1).dropLast(1)
//                    search(filters)
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