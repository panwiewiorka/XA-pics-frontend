package xapics.app.ui.common.picScreen

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import xapics.app.AppState
import xapics.app.MainViewModel
import xapics.app.ShowHide.HIDE
import xapics.app.ui.composables.ConnectionErrorButton
import xapics.app.ui.windowInfo

@OptIn(ExperimentalFoundationApi::class,)
@Composable
fun PicScreen(
    viewModel: MainViewModel,
    appState: AppState,
    goToPicsListScreen: () -> Unit,
    goToAuthScreen: () -> Unit,
) {
    val context = LocalContext.current

    val pagerState = rememberPagerState(
        initialPage = appState.picIndex ?: 0,
        initialPageOffsetFraction = 0f
    ) {
        appState.picsList?.size ?: 0
    }

    /*
//    val scope = rememberCoroutineScope()
    var animateFirstMove by remember { mutableStateOf(true) }
    var animateValue by remember { mutableStateOf(15.dp) }
    var alphaValue by remember { mutableFloatStateOf(0f) }

    val swipeArrowsOffset by animateDpAsState(
        targetValue = animateValue,
        animationSpec = tween(600, 0, easing = EaseInOutSine),
        finishedListener = {
            animateValue = (-5).dp
            animateFirstMove = false
        },
        label = "swipe offset"
    )
    val swipeArrowsAlpha by animateFloatAsState(
        targetValue = if (animateFirstMove) alphaValue else 0f,
        animationSpec = if (animateFirstMove) {
            tween(700, 200, easing = EaseInOutSine)
        } else {
            tween(1000, 200, easing = EaseInOutSine)
        },
        label = "swipe alpha",
    )

    LaunchedEffect(Unit) {
        animateValue = (-10).dp
        alphaValue = 1f
    }
     */

    LaunchedEffect(Unit) {
        viewModel.updateTopBarCaption(viewModel.stateHistory.last().topBarCaption)
        if (appState.picsList?.size == 1) {
            Toast.makeText(
                context,
                "Showing the only pic found",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.updatePicState(pagerState.currentPage)
        viewModel.updateStateSnapshot()
    }

    if (appState.connectionError.isShown) {
        ConnectionErrorButton {
            appState.picIndex?.let { viewModel.updatePicState(it) }
            viewModel.showConnectionError(HIDE)
        }
    } else {
        if (windowInfo().isPortraitOrientation) {
            PicPortraitView(viewModel, appState, pagerState, goToPicsListScreen, goToAuthScreen)
        } else {
            PicLandscapeView(viewModel, appState, pagerState)
        }
    }
}