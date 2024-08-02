package xapics.app.presentation.screens.homeScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import xapics.app.Pic
import xapics.app.R
import xapics.app.presentation.components.AsyncPic
import xapics.app.presentation.theme.AlmostWhite
import xapics.app.presentation.theme.AlphaBlack

@Composable
fun RandomPic(pic: Pic?, getRandomPic: () -> Unit, updateAndGoToPicScreen: () -> Unit, modifier: Modifier, paddingModifier: Modifier) {
    pic?.let {
        Box {
            AsyncPic(
                url = it.imageUrl,
                description = "random pic: ${it.description}",
                modifier = modifier.then(paddingModifier),
                onClick = getRandomPic
            )

            Icon(
                painterResource(R.drawable.baseline_info_outline_24),
                "Show pic info",
                tint = AlmostWhite,
                modifier = paddingModifier
                    .padding(8.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .alpha(0.7f)
                    .background(AlphaBlack)
                    .clickable { updateAndGoToPicScreen() }
                    .padding(8.dp)
            )
        }
    }
}