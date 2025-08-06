package com.linari.presentation.common.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuItemColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.PopupProperties
import com.linari.R
import com.linari.data.common.utils.mdp

@Composable
fun <T>AutoDropDownTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue)->Unit,
    suggestions: List<T>,
    onSuggestionClick: (T?)->Unit,
    modifier: Modifier = Modifier,
    showSuggestion: Boolean = true,
    clickable: Boolean = false,
    label:String = "",
    icon: Int = R.drawable.vuesax_linear_user
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }



    var textFieldSize by remember {
        mutableStateOf(IntSize.Zero)
    }
    val density = LocalDensity.current


    Column(modifier) {
        val filteredSuggestions = if(clickable) suggestions else  suggestions.filter { it.toString().lowercase().contains(value.text.lowercase()) }

        InputTextField(
            value = value,
            onValueChange = {
                if (it.text.isEmpty()){
                    onSuggestionClick(null)
                }
                onValueChange(it)
                    isExpanded = it.text.isNotEmpty()
            },
            label = label,
            readOnly = clickable,
            onClick = {
                isExpanded = !isExpanded
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.onGloballyPositioned {
                textFieldSize = it.size
            },
            leadingIcon = icon
        )
        Box {
            DropdownMenu(
                expanded = isExpanded && suggestions.isNotEmpty() && showSuggestion && (!suggestions.any { it.toString() == value.text } || clickable),
                properties = PopupProperties(focusable = false),
                containerColor = Color.White,
                modifier = Modifier.heightIn(max = 200.mdp),
                onDismissRequest = { isExpanded = false },
                scrollState = rememberScrollState()
            ) {
                filteredSuggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        onClick = {
                            onValueChange(TextFieldValue(suggestion.toString()))
                            isExpanded = false
                            onSuggestionClick(suggestion)
                        },
                        modifier = Modifier.width(
                            with(density) {
                                textFieldSize.width.toDp()
                            }
                        ),
                        text = {
                            MyText(text = suggestion.toString())
                        }
                    )
                }
            }
        }
        LaunchedEffect(key1 = filteredSuggestions) {
            if(filteredSuggestions.firstOrNull()?.toString() == value.text){
                onSuggestionClick(filteredSuggestions.first())
                isExpanded = false
            }
        }
    }
}


@Preview
@Composable
private fun AutoDropDownTextFieldPreview() {
    val suggestion = listOf("test1", "test2", "test3","ok","okt")
    var value by remember {
        mutableStateOf(TextFieldValue())
    }
    MyScaffold {

        AutoDropDownTextField(
            value,
            onValueChange = {value = it},
            suggestions = suggestion,
            onSuggestionClick = {}
        )
    }
}