package xapics.app.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RollCard(width: Dp, isLoading: Boolean, imageUrl: String, rollTitle: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
//            .background(PicBG)
            .padding(1.dp)
            .clip(RoundedCornerShape(15.dp))
            .clickable {
                onClick()
            }
    ) {
        CircularProgressIndicator() // TODO remove?

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Thumbnail of the $rollTitle roll",
                modifier = Modifier
                    .width(width)
                    .height((width.value / 1.5).dp)
//                    .height(100.dp)
//                    .width((100 * 1.5).dp)
            )
            Text(text = "  $rollTitle  ", maxLines = 1, modifier = Modifier.basicMarquee())
        }

        if(isLoading) {
            CircularProgressIndicator() // FIXME
        }
    }
}