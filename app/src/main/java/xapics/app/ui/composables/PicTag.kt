package xapics.app.ui.composables

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xapics.app.Tag
import xapics.app.ui.theme.CollectionTag
import xapics.app.ui.theme.DefaultTag
import xapics.app.ui.theme.FilmTag
import xapics.app.ui.theme.GrayMedium
import xapics.app.ui.theme.RollAttribute
import xapics.app.ui.theme.YearTag

@Composable
fun PicTag(tag: Tag, onClick: () -> Unit) {

    val (color, text) = when (tag.type) {
        "filmType" -> Pair(GrayMedium, tag.value.lowercase())
        "nonXa" -> Pair(RollAttribute, if(tag.value == "false") "XA" else "non-XA")
        "expired" -> Pair(RollAttribute, if(tag.value == "false") "not expired" else "expired")
        "xpro" -> Pair(RollAttribute, if(tag.value == "false") "no cross-process" else "cross-process")
        "iso" -> Pair(GrayMedium, "iso ${tag.value}")
        "filmName" -> Pair(FilmTag, tag.value)
        "year" -> Pair(YearTag, tag.value)
        "hashtag" -> Pair(DefaultTag, tag.value)
        "collection" -> Pair(CollectionTag, tag.value)
        else -> Pair(Color.Transparent, tag.value)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .padding(bottom = 8.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .border(1.dp, color, CircleShape)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}