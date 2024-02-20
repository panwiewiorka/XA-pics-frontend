package xapics.app.ui.theme

import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)


val AlmostWhite = Color(0xffe6e6e6)
val MyPrimary = Color(0xff55BBFF)
val MyError = Color(0xffF33D3D)
val GrayLighter = Color(0xFFBBBBBB)
val GrayLight = Color(0xFF999999)
val GrayMedium = Color(0xFF555555)
val GrayDark = Color(0xFF333333)
val BG = Color(0xFF222222)
val AlphaBlack = Color(0x55000000)

val FilmTag = Color(0x77FF5549)
val YearTag = Color(0x77559977)
val CollectionTag = Color(0x77BBBB77)
val DefaultTag = Color(0x7744AAFF)

@Composable
fun myTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        focusedTextColor = AlmostWhite,
        unfocusedTextColor = AlmostWhite,
        focusedContainerColor = AlphaBlack,
        unfocusedContainerColor = AlphaBlack,
        unfocusedIndicatorColor = AlmostWhite,
        unfocusedLabelColor = AlmostWhite,
        focusedTrailingIconColor = AlmostWhite,
        unfocusedTrailingIconColor = GrayLight,
    )
}