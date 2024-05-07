package xapics.app.ui.common.homeScreen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xapics.app.R
import xapics.app.Tag
import xapics.app.TagState
import xapics.app.ui.composables.PicTag
import xapics.app.ui.theme.AlmostWhite
import xapics.app.ui.theme.AlphaBlack

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagsCloud(
    scrollState: ScrollState,
    tags: List<Tag>,
    search: (String) -> Unit,
    goToPicsListScreen: () -> Unit,
    goToSearchScreen: () -> Unit,
    padding: Dp,
) {
    Box(modifier = Modifier) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(start = padding, top = padding)
        ) {
            FlowRow {
                val tagsMapped = tags.map {
                    Tag(
                        it.type,
                        it.value,
                        TagState.ENABLED
                    )
                }
                tagsMapped.forEach {
                    PicTag(it) {
                        search("${it.type} = ${it.value}")
                        goToPicsListScreen()
                    }
                }
            }
        }

        Gradient(top = true)
        Gradient(top = false, Modifier.align(Alignment.BottomCenter))

        Icon(
            painterResource(R.drawable.baseline_unfold_more_24),
            contentDescription = "Go to search screen",
            tint = AlmostWhite,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.BottomEnd)
                .clip(CircleShape)
                .alpha(0.7f)
                .background(AlphaBlack)
                .clickable { goToSearchScreen() }
                .padding(8.dp)
        )
    }
}

@Composable
fun Gradient(top: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .rotate(if(top) 0f else 180f)
            .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.background, Color.Transparent)))
    ) { }
}