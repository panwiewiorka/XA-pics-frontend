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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import xapics.app.ui.theme.PicBG

@Composable
fun PicsListScreen(
    viewModel: MainViewModel, appState: AppState, goToPicScreen: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        val scrollState = rememberLazyListState()
        Row {
            LazyColumn(
                state = scrollState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(appState.picsList?.size ?: 0) {
                    BoxWithConstraints {
                        val height = ((maxWidth - 64.dp).value / 1.5).dp
                        val pic = appState.picsList!![it]
                        PicItem(
                            pic = pic,
                            height = height,
                            onClick = {
//                                viewModel.getPicCollections(pic.id)
                                viewModel.updatePicState(it)
                                goToPicScreen()
                            }
                        )
                    }
                }
            }
        }

        if(appState.isLoading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun PicItem(
    pic: Pic,
    height: Dp,
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Spacer(modifier = Modifier.width(32.dp))
        Picture(
            pic,
            onClick,
            modifier = Modifier
                .weight(1f)
                .height(height)
        )
        Spacer(modifier = Modifier.width(32.dp))
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
fun Picture(pic: Pic, onClick: () -> Unit, modifier: Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(PicBG)
    ) {
        CircularProgressIndicator() // TODO remove?

        AsyncImage(
            modifier = modifier
                .clip(RoundedCornerShape(2.dp))
                .clickable { onClick() },
            model = ImageRequest.Builder(LocalContext.current)
                .data(pic.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = pic.description,
        )
    }

}