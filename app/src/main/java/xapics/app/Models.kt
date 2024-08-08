package xapics.app

import androidx.annotation.StringRes


enum class TagState { DISABLED, ENABLED, SELECTED }

enum class OnPicsListScreenRefresh { SEARCH, GET_COLLECTION }

enum class NavList(@StringRes val title: Int) {
    HomeScreen(title = R.string.home_screen),
    PicsListScreen(title = R.string.pics_list_screen),
    PicScreen(title = R.string.pic_screen),
    SearchScreen(title = R.string.search_screen),
    AuthScreen(title = R.string.auth_screen),
    ProfileScreen(title = R.string.profile_screen),
}


data class Pic(
    val id: Int,
    val imageUrl: String,
    val description: String,
    val keywords: String,
    val tags: String,
//    val collections: List<String> = emptyList(),
)

data class Thumb(
    val title: String,
    val thumbUrl: String
)

// needed to fix this (probably due to String containing special symbols):
// com.google.gson.stream.MalformedJsonException: Use JsonReader.setLenient(true) to accept malformed JSON at line 1 column 1 path $
data class TheString(
    val string: String
)

data class Tag(
    val type: String,
    val value: String,
    var state: TagState = TagState.ENABLED
)