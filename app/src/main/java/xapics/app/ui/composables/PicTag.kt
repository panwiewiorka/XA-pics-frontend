package xapics.app.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xapics.app.Tag
import xapics.app.TagState.DISABLED
import xapics.app.TagState.SELECTED

@Composable
fun PicTag(tag: Tag, getTagColorAndName: (Tag) -> Pair<Color, String>, onClick: () -> Unit) {

    val (color, text) = getTagColorAndName(tag)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .padding(bottom = 8.dp)
            .clip(CircleShape)
            .clickable(enabled = tag.state != DISABLED) {
                onClick()
            }
            .background(if (tag.state == SELECTED) color else Color.Transparent, CircleShape)
            .border(1.dp, if (tag.state == DISABLED) Color.Transparent else color, CircleShape)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .alpha(if (tag.state == DISABLED) 0.3f else 1f)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = Ellipsis
        )
    }
}