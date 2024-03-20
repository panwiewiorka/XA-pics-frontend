package xapics.app.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import xapics.app.data.PicsApi.Companion.BASE_URL

@Composable
fun AsyncPic(url: String, description: String, modifier: Modifier, onClick: (() -> Unit)? = null) {
    var showRetryButton by remember {
        mutableStateOf(false)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(MaterialTheme.colorScheme.tertiary)
            .aspectRatio(3f / 2f, false)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(BASE_URL + "files/images/" + url)
                .listener(
                    onError = { _, _ -> showRetryButton = true },
                    onSuccess = {_, _ -> showRetryButton = false}
                )
                .crossfade(200)
                .build(),
            contentDescription = description,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = onClick != null) { onClick!!() },
        )

        if (showRetryButton) {
            Icon(Icons.Default.Refresh, "retry loading image")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        showRetryButton = false
                    }
            )
        }
    }
}