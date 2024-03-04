package xapics.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xapics.app.AppState
import xapics.app.MainViewModel
import xapics.app.ui.composables.PicTag
import xapics.app.ui.composables.PicTagsList

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    viewModel: MainViewModel, appState: AppState, goToPicsListScreen: () -> Unit, // TODO replace with only essentials
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        FlowRow(
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 12.dp)
        ) {
            appState.tags.forEach {
                PicTag(it) {
                    viewModel.getFilteredTags(it)
                }
            }
        }
    }
}