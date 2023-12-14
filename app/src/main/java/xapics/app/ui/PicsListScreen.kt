package xapics.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import xapics.app.AppState
import xapics.app.MainViewModel
import xapics.app.Pic

@Composable
fun PicsListScreen(
    viewModel: MainViewModel, appState: AppState, goToPicScreen: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        val sst = rememberLazyListState()
        Row {
//            LazyColumn(
//                state = sst,
//                modifier = Modifier) {
//                items(5) {
//                    Perforation(h = 310.dp)
//                }
//            }

            LazyColumn(
                state = sst,
                modifier = Modifier
                    .fillMaxSize()
//                .padding(32.dp)
//                    .align(Alignment.TopCenter)
            ) {
                items(appState.picsList?.size ?: 0) {
                    BoxWithConstraints {
                        val height = ((maxWidth - 64.dp).value / 1.5).dp
                        PicItem(picIndex = it, pic = appState.picsList!![it], updatePicState = viewModel::updatePicState, goToPicScreen, height)
                    }
                }
            }
        }


        Row(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Button(
                onClick = {
                    viewModel.getPicsList(film = "Ektachrome")
                },
            ) {
                Text("Ektachrome")
            }
            Button(
                onClick = {
                    viewModel.getPicsList(film = "Aerocolor")
                },
            ) {
                Text("Aerocolor")
            }
            Button(
                onClick = {
                    viewModel.getPicsList(year = 2021)
                },
            ) {
                Text("2021")
            }
            Button(
                onClick = {
                    viewModel.getPicsList(year = 2023)
                },
            ) {
                Text("2023")
            }
        }

        if(appState.isLoading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun PicItem(
    picIndex: Int,
    pic: Pic,
    updatePicState: (Int) -> Unit,
    goToPicScreen: () -> Unit,
    height: Dp
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Perforation(height)
        Spacer(modifier = Modifier.width(16.dp))
        Picture(picIndex, pic, updatePicState, goToPicScreen, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(16.dp))
        Perforation(height)
    }
}

@Composable
fun Perforation(height: Dp) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.height(height)
    ) {
        repeat(7) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF505050))
            )
        }
    }
}

@Composable
fun Perf(height: Dp) {

}

@Composable
fun Picture(picIndex: Int, pic: Pic, updatePicState: (Int) -> Unit, goToPicScreen: () -> Unit, modifier: Modifier) {
    AsyncImage(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .clickable {
                updatePicState(picIndex)
                goToPicScreen()
            },
        model = ImageRequest.Builder(LocalContext.current)
            .data(pic.imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = pic.description,
    )
}