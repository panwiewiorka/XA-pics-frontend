package xapics.app.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RollCard(width: Dp, isLoading: Boolean, imageUrl: String, rollTitle: String, onClick: () -> Unit) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(12.dp)
        ) {
            AsyncPic(
                url = imageUrl,
                description = "Click to open $rollTitle roll",
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp))
                    .width(width)
                    .height((width.value / 1.5).dp)
                    .clip(RoundedCornerShape(16.dp)),
            ) {
                onClick()
            }

            Text(
                text = "  $rollTitle  ",
                maxLines = 1,
                overflow = Ellipsis,
                modifier = Modifier
                    .widthIn(max = width)
//                    .basicMarquee()
                    .clip(CircleShape)
                    .clickable {
                        onClick()
                    }
            )
        }
}