package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.R
import com.example.jaywarehouse.ui.theme.Border
import kotlinx.coroutines.delay
import java.lang.StringBuilder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchInput(
    modifier: Modifier = Modifier,
    onSearch: (TextFieldValue) -> Unit = {},
    value: String = "",
    isLoading: Boolean = false,
    showSortIcon: Boolean = true,
    onSortClick: () -> Unit = {},
    hideKeyboard: Boolean = true,
    focusRequester: FocusRequester = FocusRequester.Default
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isFocused by remember { mutableStateOf(false) }
    val isKeyboardOpen = WindowInsets.isImeVisible

    var keyword by remember {
        mutableStateOf(TextFieldValue(value))
    }

    LaunchedEffect(value) {
        if (value.isEmpty()){
            keyword = TextFieldValue()
        }
    }

    LaunchedEffect(key1 = isFocused,isKeyboardOpen) {
        if ((isFocused || isKeyboardOpen) && hideKeyboard){
            keyboardController?.hide()
        }
    }
    SideEffect {
        if ((isFocused || isKeyboardOpen) && hideKeyboard) {
            keyboardController?.hide()
        }
    }
//    if (isFocused && hideKeyboard) SideEffect {
//        hideKeyboard(activity)
//    }

    AnimatedVisibility(
        true,
        enter = slideInVertically(initialOffsetY = {it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = {it}) + fadeOut()
    ) {
        Box {
            BasicTextField(
                keyword,
                onValueChange = {
                    if(!isLoading){
                        keyword = it
                        if (it.text.endsWith('\n') || it.text.endsWith('\r')) {
                            onSearch(it)
                        }
                    }
                },
                modifier = Modifier
                    .onKeyEvent {
                        if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
                            onSearch(keyword)
                            true
                        } else {
                            false
                        }
                    }
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused && hideKeyboard) {
                            keyboardController?.hide()
                        }
                        isFocused = it.isFocused
                    }

                ,
                maxLines = 1,
                decorationBox = {
                    Row(
                        modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.mdp))
                            .background(Color.White)
                            .border(1.mdp, Border, RoundedCornerShape(6.mdp))
                            .padding(vertical = 9.mdp, horizontal = 10.mdp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart){
                                if (keyword.text.isEmpty()) {
                                    MyText(
                                        text = "Search Keyword ...",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.LightGray
                                    )
                                }
                                it()
                                if (isFocused && hideKeyboard)Box(modifier = Modifier
                                    .matchParentSize()
                                    .fillMaxWidth()
                                    .clip(
                                        RoundedCornerShape(10.mdp)
                                    )
                                    .clickable { })
                            }

                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (keyword.text.isNotEmpty()) MyIcon(icon = R.drawable.vuesax_bulk_broom) {
//                                onValueChange(TextFieldValue())
                                onSearch(TextFieldValue())
                                keyword = TextFieldValue()
                            }
                            Spacer(modifier = Modifier.size(5.mdp))
                            AnimatedContent(targetState = isLoading, label = "") {
                                if (it){
                                    RefreshIcon(isRefreshing = true)
                                }else {
                                    MyIcon(icon = R.drawable.vuesax_linear_search_normal) {
                                        onSearch(keyword)
                                    }
                                }
                            }
                            if (showSortIcon)Spacer(modifier = Modifier.size(5.mdp))
                            if (showSortIcon)MyIcon(icon = R.drawable.vuesax_linear_sort) {
                                onSortClick()
                            }
                        }
                    }
                }
            )

        }
    }
}