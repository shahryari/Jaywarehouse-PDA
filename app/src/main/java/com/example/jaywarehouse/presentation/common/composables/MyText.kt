package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.example.jaywarehouse.data.common.utils.withEnglishDigits
import com.example.jaywarehouse.localWindowFactor
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.roboto

@Composable
fun MyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Black,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = FontWeight.W500,
    fontFamily: FontFamily = roboto,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current
) {
    val factor = localWindowFactor.current
    Text(
        text.withEnglishDigits(),
        modifier,
        color,
        if(fontSize != TextUnit.Unspecified)fontSize.times(factor) else fontSize,
        fontStyle,
        fontWeight,
        fontFamily,
        letterSpacing,
        textDecoration,
        textAlign,
        lineHeight,
        overflow,
        softWrap,
        maxLines,
        minLines,
        onTextLayout,
        if (style == LocalTextStyle.current) style else style.copy(fontSize = style.fontSize.times(factor))
    )
}