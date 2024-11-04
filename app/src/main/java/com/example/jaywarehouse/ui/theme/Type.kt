package com.example.jaywarehouse.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.jaywarehouse.R


val poppins = FontFamily(listOf(
    Font(R.font.poppingslation_black, FontWeight.Black),
    Font(R.font.poppingslation_bold, FontWeight.Bold),
    Font(R.font.poppingslation_italic, style = FontStyle.Italic),
    Font(R.font.poppingslation_extralight, FontWeight.ExtraLight),
    Font(R.font.poppingslation_light, FontWeight.Light),
    Font(R.font.poppingslation_regular),
    Font(R.font.poppingslation_semibold, FontWeight.SemiBold),
    Font(R.font.poppingslation_extrabold, FontWeight.ExtraBold),
    Font(R.font.poppingslation_medium, FontWeight.Medium),
    Font(R.font.poppingslation_thin, FontWeight.Thin)
))

val roboto = FontFamily(listOf(
    Font(R.font.roboto_black, FontWeight.Black),
    Font(R.font.roboto_bold, FontWeight.Bold),
    Font(R.font.roboto_italic, style = FontStyle.Italic),
    Font(R.font.roboto_light, FontWeight.Light),
    Font(R.font.roboto_regular),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_thin, FontWeight.Thin)
))


// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)