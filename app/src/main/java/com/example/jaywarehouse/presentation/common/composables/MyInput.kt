package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
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
import com.example.jaywarehouse.ui.theme.Gray1
import com.example.jaywarehouse.ui.theme.poppins

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MyInput(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue)->Unit,
    onAny: ()->Unit = {},
    label: String = "",
    leadingIcon: (@Composable ()->Unit)? = null,
    trailingIcon: (@Composable ()->Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    hideKeyboard: Boolean = true,
    focusRequester: FocusRequester = FocusRequester.Default,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    var isFocused by remember { mutableStateOf(false) }
    val isKeyboardOpen = WindowInsets.isImeVisible


    LaunchedEffect(key1 = isFocused,isKeyboardOpen,hideKeyboard) {
        if ((isFocused || isKeyboardOpen) && hideKeyboard){
            keyboard?.hide()
        }
    }
    SideEffect {
        if ((isFocused || isKeyboardOpen) && hideKeyboard){
            keyboard?.hide()
        }
    }
//    val activity = LocalContext.current as MainActivity
//    SideEffect {
//        activity.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
//    }
//    if (isFocused && hideKeyboard) SideEffect {
//        hideKeyboard(activity)
//    }
    Box(modifier = modifier){
        BasicTextField(value,
            onValueChange = {
                if (!readOnly){
                    if (it.text.endsWith('\n') || it.text.endsWith('\r')) {
                        onAny()
                    } else {
                        onValueChange(it)
                    }
                }
            },
            modifier = Modifier
                .onKeyEvent {
                    if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
                        onAny()
                        true
                    } else {
                        false
                    }
                }
                .focusRequester(focusRequester = focusRequester)
                .onFocusChanged {
                    if (it.isFocused && hideKeyboard) {
                        keyboard?.hide()
                    }
                    isFocused = it.isFocused
                }
            ,
            enabled = enabled,
            maxLines = 1,
            decorationBox = {
                Row(
                    modifier
                        .shadow(2.mdp, RoundedCornerShape(10.mdp))
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.mdp))
                        .background(if (enabled) Color.White else Gray1)
//                    .border(
//                        1.mdp, MaterialTheme.colorScheme.outlineVariant,
//                        RoundedCornerShape(10.mdp)
//                    )
                        .padding(vertical = 9.mdp, horizontal = 10.mdp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.weight(1f),verticalAlignment = Alignment.CenterVertically) {
                        if (leadingIcon!=null)leadingIcon()
                        if (leadingIcon!=null)Spacer(modifier = Modifier.size(10.mdp))
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (value.text.isEmpty()) {
                                MyText(
                                    text = label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontFamily = poppins,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.LightGray
                                )
                            }
                            it()
                            if (isFocused && hideKeyboard)Box(modifier = Modifier
                                .fillMaxWidth()
                                .matchParentSize()
                                .clip(
                                    RoundedCornerShape(10.mdp)
                                ).clickable {  }
                            )
                        }

                    }
                    Box{
                        if (trailingIcon!=null) trailingIcon()
                    }
                }
            }
        )
    }


}

