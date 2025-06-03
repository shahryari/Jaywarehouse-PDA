package com.example.jaywarehouse.presentation.common.composables

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.ui.theme.Border
import com.example.jaywarehouse.ui.theme.Gray1
import com.example.jaywarehouse.ui.theme.Primary
import com.example.jaywarehouse.ui.theme.Red
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InputTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    onAny: () -> Unit = {},
    onClick: () -> Unit = {},
    label: String = "",
    suffix: String = "",
    prefix: String = "",
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    decimalInput: Boolean = false,
    onLeadingClick: (() -> Unit)? = null,
    onTrailingClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    required: Boolean = false,
    hideKeyboard: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    focusRequester: FocusRequester = remember { FocusRequester() },
    loading: Boolean = false
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    var isFocused by remember { mutableStateOf(false) }
    val isKeyboardOpen = WindowInsets.isImeVisible

    var keyword by remember { mutableStateOf(value) }

    // Sync internal state if external value changes
    LaunchedEffect(value) {
        if (value.text != keyword.text) {
            keyword = value
        }
    }

    // Debounce input change to avoid frequent recomposition
    var debounceJob by remember { mutableStateOf<Job?>(null) }
    LaunchedEffect(keyword.text) {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(150)
            onValueChange(keyword)
        }
    }

    // Auto-focus to receive PDA scanner input
    LaunchedEffect(Unit) {
        delay(100) // Give time for composition
        focusRequester.requestFocus()
    }

    LaunchedEffect(isFocused, isKeyboardOpen, hideKeyboard) {
        if ((isFocused || isKeyboardOpen) && hideKeyboard) {
            keyboard?.hide()
        }
    }

    Box(modifier = modifier) {
        BasicTextField(
            value = keyword,
            onValueChange = {
                if (!readOnly) {
                    if (it.text.endsWith('\n')) {
                        keyword = it
                        onAny()
                    } else {
                        if (keyboardOptions.keyboardType == KeyboardType.Number && !decimalInput) {
                            if (it.text.all { c -> c.isDigit() }) {
                                keyword = it
                            }
                        } else {
                            keyword = it
                        }
                    }
                }
            },
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                    if (it.isFocused && hideKeyboard) {
                        keyboard?.hide()
                    }
                }
                .onKeyEvent {
                    if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
                        onAny()
                        true
                    } else false
                },
            enabled = enabled,
            readOnly = readOnly,
            keyboardOptions = keyboardOptions,
            maxLines = 1,
            decorationBox = {
                Row(
                    modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.mdp))
                        .background(if (enabled) if (required && keyword.text.isEmpty()) Red.copy(0.1f) else Color.White else Gray1)
                        .border(
                            1.mdp,
                            if (required && keyword.text.isEmpty() && enabled) Red else if (isFocused) Primary else Border,
                            RoundedCornerShape(6.mdp)
                        )
                        .then(
                            if (readOnly)
                                Modifier.clickable(enabled) { onClick() }
                            else Modifier
                        )
                        .padding(vertical = 9.mdp, horizontal = 10.mdp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        if (leadingIcon != null) {
                            MyIcon(icon = leadingIcon, showBorder = false, onClick = onLeadingClick)
                            Spacer(modifier = Modifier.size(7.mdp))
                        }
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (keyword.text.isEmpty()) {
                                MyText(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    color = Color.LightGray
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MyText(
                                    prefix,
                                    maxLines = 1,
                                    style = TextStyle.Default,
                                    fontWeight = FontWeight.Normal
                                )
                                it()
                            }
                            if (isFocused && hideKeyboard) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .matchParentSize()
                                        .clip(RoundedCornerShape(10.mdp))
                                        .clickable { }
                                )
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (suffix.isNotEmpty()) {
                            MyText(
                                text = suffix,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.size(7.mdp))
                        }
                        if (enabled && keyword.text.isNotEmpty()) {
                            MyIcon(icon = R.drawable.vuesax_bulk_broom, showBorder = false) {
                                keyword = TextFieldValue()
                                onValueChange(TextFieldValue())
                            }
                        }
                        Spacer(modifier = Modifier.size(7.mdp))
                        if (loading) {
                            RefreshIcon(isRefreshing = true)
                        } else if (trailingIcon != null) {
                            MyIcon(icon = trailingIcon, onClick = onTrailingClick)
                        }
                    }
                }
            }
        )
    }
}
