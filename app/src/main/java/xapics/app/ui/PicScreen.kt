package xapics.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import xapics.app.AppState
import xapics.app.MainViewModel
import xapics.app.PicType

@Composable
fun PicScreen(
    viewModel: MainViewModel, appState: AppState, goToPicsListScreen: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
//            LaunchedEffect(appState.picsList) {
//                viewModel.getPic(PicType.FIRST)
//            }
            if (appState.picIndex != null && appState.picsList != null && appState.pic != null) {

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(appState.pic.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = appState.pic.description,
                )

                Text(text = "${appState.picIndex + 1} / ${appState.picsList.size}")
                Text(text = appState.pic.description)

                Row {
                    Button(
                        onClick = {viewModel.getPic(PicType.PREV)},
                        enabled = appState.picIndex > 0
                    ) {
                        Text("<")
                    }
                    Button(
                        onClick = {viewModel.getPic(PicType.NEXT)},
                        enabled = (appState.picIndex < appState.picsList.lastIndex)
                    ) {
                        Text(">")
                    }
                }
            }

            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                PicTag(appState.pic?.film ?: "", Color(0xFFFF5549)) {
                    viewModel.getPicsList(film = appState.pic?.film)
                    goToPicsListScreen()
                }

                PicTag(appState.pic?.year?.toString() ?: "", Color(0xFF559977)) {
                    viewModel.getPicsList(year = appState.pic?.year)
                    goToPicsListScreen()
                }

                val tags = appState.pic?.tags?.split(',')

                tags?.forEach { tag ->
                    val tt = tag.trim()
                    PicTag(tt) {
                        viewModel.getPicsList(tag = tt)
                        goToPicsListScreen()
                    }
                }

                /**
                Button(
                onClick = {
                viewModel.getPicsList(film = appState.pic?.film)
                goToPicsListScreen()
                },
                ) {
                Text(appState.pic?.film ?: "")
                }
                Button(
                onClick = {
                viewModel.getPicsList(year = appState.pic?.year)
                goToPicsListScreen()
                },
                ) {
                Text(appState.pic?.year?.toString() ?: "")
                }
                 */
            }

            if(appState.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun PicTag(text: String, color: Color? = null, query: () -> Unit) {
    Button(
        onClick = query,
        colors = ButtonDefaults.buttonColors(
            containerColor = color ?: Color(0xFF44AAFF)
        )
    ) {
        Text(text)
    }
}