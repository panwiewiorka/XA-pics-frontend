package xapics.app.ui

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import xapics.app.AppState
import xapics.app.MainViewModel
import xapics.app.R
import xapics.app.TAG
import xapics.app.Tag
import xapics.app.data.PicsApi.Companion.BASE_URL
import xapics.app.ui.composables.AsyncPic
import xapics.app.ui.composables.CollectionsDropDownMenu
import xapics.app.ui.composables.ConnectionErrorButton
import xapics.app.ui.composables.PicTag

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,)
@Composable
fun PicScreen(
    viewModel: MainViewModel, appState: AppState, goToPicsListScreen: () -> Unit, goToAuthScreen: () -> Unit,
) {
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

    LaunchedEffect(pagerState.currentPage) {
        viewModel.updatePicState(pagerState.currentPage)
        Log.d(TAG, "PicScreen: pagerState = $pagerState.currentPage")
    }

    if (appState.showConnectionError) {
        ConnectionErrorButton {
            appState.picIndex?.let { viewModel.updatePicState(it) }
            viewModel.changeConnectionErrorVisibility(false)
        }
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp)
        ) {
            if (appState.picIndex != null && appState.picsList != null && appState.pic != null) { // TODO

                Box {

                    /*
                    if (pagerState.currentPage > 0) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .offset(x = -swipeArrowsOffset)
                                .alpha(swipeArrowsAlpha)
                        )
                    }

                    if (pagerState.currentPage < appState.picsList.lastIndex) {
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .offset(x = swipeArrowsOffset)
                                .alpha(swipeArrowsAlpha)
                        )
                    }

                     */

                    HorizontalPager(
                        state = pagerState,
                        pageSize = PageSize.Fill,
//                        key = { appState.picsList[it].id } // FIXME crashes when clicking TAGS
                    ) {index ->
                        val pic = appState.picsList[index]
                        BoxWithConstraints(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val height = (maxWidth.value / 1.5).dp
                            AsyncPic(
                                url = BASE_URL + pic.imageUrl,
                                description = pic.description,
                                modifier = Modifier
                                    .height(height)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.padding(start = 32.dp, end = 16.dp).fillMaxWidth()
                    ) {
                        Column {
                            Text(text = "${appState.picIndex + 1} / ${appState.picsList.size}")
                            Text(text = appState.pic.description, overflow = Ellipsis)
                        }
                        Spacer(modifier = Modifier.weight(1f))

                        Box {
                            CollectionsDropDownMenu(
                                appState.userCollections,
                                appState.picCollections,
                                appState.collectionToSaveTo,
                                appState.pic.id,
                                viewModel::editCollectionOrLogIn,
                                viewModel::updateCollectionToSaveTo,
                            ) { goToAuthScreen() }
                        }

                        IconButton(
                            onClick = {
                                viewModel.editCollectionOrLogIn(
                                    appState.collectionToSaveTo,
                                    appState.pic.id,
                                    goToAuthScreen
                                )
                            },
                        ) {
                            val collection = appState.collectionToSaveTo
                            val picInCollection = appState.picCollections.contains(collection)
                            if (collection == stringResource(R.string.fav_collection)) {
                                if (picInCollection) {
                                    Icon(Icons.Filled.Favorite, "Remove from $collection")
                                } else {
                                    Icon(Icons.Outlined.FavoriteBorder, "Add to $collection")
                                }
                            } else {
                                if (picInCollection) {
                                    Icon(Icons.Filled.Star, "Remove from $collection")
                                } else {
                                    Icon(
                                        painterResource(id = R.drawable.star_border),
                                        "Add to $collection"
                                    )
                                }
                            }
                        }
                    }

                    AnimatedContent(
                        targetState = appState,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200, 50))
                        },
                        label = "tags"
                    ) { state ->
                        FlowRow(
                            modifier = Modifier.padding(horizontal = 28.dp)
                        ) {
                            if(state.pic != null) {
                                val tags = state.pic.tags
                                    .split(", ")
                                    .map { it.split(" = ") }
                                    .map { Tag(it[0], it[1]) }

                                tags.forEach {
                                    PicTag(it) {
                                        viewModel.getPicsList("${it.type} = ${it.value}")
                                        goToPicsListScreen()
                                    }
                                }
                            }

                            state.picCollections.forEach {
                                PicTag(Tag("collection", it)) {
                                    viewModel.getCollection(it)
                                    goToPicsListScreen()
                                }
                            }
                        }
                    }
                }
            }

//            if(appState.isLoading) {
//                CircularProgressIndicator()
//            }
        }
    }
}