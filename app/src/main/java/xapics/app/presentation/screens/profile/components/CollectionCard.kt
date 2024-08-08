package xapics.app.presentation.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import xapics.app.R
import xapics.app.presentation.components.RollCard
import xapics.app.presentation.theme.AlmostWhite
import xapics.app.presentation.theme.AlphaBlack

@Composable
fun CollectionCard(
    thumbUrl: String,
    rollTitle: String,
    favs: String,
    isPortrait: Boolean,
    onClick: () -> Unit,
    onRenameCollection: () -> Unit,
    onDeleteFavourites: () -> Unit,
) {
    Box {
        RollCard(
            imageUrl = thumbUrl,
            rollTitle = rollTitle,
            isPortrait = isPortrait,
            modifier = Modifier.padding(12.dp),
            onClick = onClick
        )

        val modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(-(13).dp, 13.dp)
            .clip(RoundedCornerShape(15.dp))
            .alpha(0.7f)
            .background(AlphaBlack)

        if (rollTitle == favs) {
            IconButton(
                modifier = modifier,
                onClick = onDeleteFavourites
            ) {
                Icon(
                    painterResource(R.drawable.baseline_delete_outline_24),
                    "Delete collection",
                    tint = AlmostWhite
                )
            }
        } else {
            IconButton(
                modifier = modifier,
                onClick = onRenameCollection
            ) {
                Icon(
                    painterResource(R.drawable.baseline_edit_24),
                    "Rename or delete collection",
                    tint = AlmostWhite
                )
            }
        }
    }
}