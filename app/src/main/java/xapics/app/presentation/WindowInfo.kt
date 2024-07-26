package xapics.app.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp


@Composable
fun windowInfo(): WindowInfo {
    val configuration = LocalConfiguration.current
    val isPortraitOrientation = configuration.screenHeightDp.dp > configuration.screenWidthDp.dp

    return WindowInfo(
        isPortraitOrientation = isPortraitOrientation,
        windowType = if (isPortraitOrientation) {
            when {
                configuration.screenWidthDp < 600 -> WindowInfo.WindowType.Compact
                configuration.screenWidthDp < 840 -> WindowInfo.WindowType.Medium
                else -> WindowInfo.WindowType.Expanded
            }
        } else {
            when {
                configuration.screenHeightDp < 480 -> WindowInfo.WindowType.Compact
                configuration.screenHeightDp < 900 -> WindowInfo.WindowType.Medium
                else -> WindowInfo.WindowType.Expanded
            }
        },
//        lowestDimension = if(isPortraitOrientation) configuration.screenWidthDp.dp else configuration.screenHeightDp.dp,
    )
}

data class WindowInfo(
    val isPortraitOrientation: Boolean,
    val windowType: WindowType,
//    val lowestDimension: Dp,
) {
    sealed class WindowType {
        object Compact: WindowType()
        object Medium: WindowType()
        object Expanded: WindowType()
    }
}