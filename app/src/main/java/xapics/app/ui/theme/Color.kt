package xapics.app.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AlmostWhite = Color(0xffe6e6e6)
val Primary = Color(0xff55BBFF)
val TextButtons = Color(0xff45A5EF)
val MyError = Color(0xffF33D3D)
val GrayLighter = Color(0xFFBBBBBB)
val GrayLight = Color(0xFF999999)
val GrayMedium = Color(0xFF555555)
val GrayDark = Color(0xFF333333)
val BG = Color(0xFF222222)

val AlphaBlack = Color(0x55000000)
val AlphaGray = Color(0x8F8F8F8F)

val FilmTag = Color(0x77FF5549)
val RollTag = Color(0x774955FF)
val YearTag = Color(0x77559977)
val CollectionTag = Color(0x77BBBB77)
val RollAttribute = Color(0x77BB00FF)
val DefaultTag = Color(0x7744AAFF)

@Composable
fun myTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = AlmostWhite,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        unfocusedIndicatorColor = AlmostWhite,
        unfocusedLabelColor = AlmostWhite,
        focusedTrailingIconColor = Color.White,
        unfocusedTrailingIconColor = GrayLight,
    )
}

@Composable
fun myTextButtonColors(): ButtonColors {
    return ButtonDefaults.textButtonColors(
        containerColor = Color.Transparent,
        contentColor = TextButtons,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = MaterialTheme.colorScheme.secondary
    )
}