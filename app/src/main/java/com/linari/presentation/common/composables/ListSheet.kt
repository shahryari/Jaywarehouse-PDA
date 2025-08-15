package com.linari.presentation.common.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import com.linari.R
import com.linari.data.auth.models.WarehouseModel
import com.linari.data.common.utils.mdp
import com.linari.presentation.common.utils.Selectable
import com.linari.ui.theme.Gray1
import com.linari.ui.theme.Primary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T: Selectable> ListSheet(
    show: Boolean,
    title: String = "",
    onDismiss: ()-> Unit,
    list: List<T>,
    selectedItem: T?,
    searchable: Boolean = false,
    onSelect:(T)-> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    if (show){
        var keyword by remember {
            mutableStateOf(TextFieldValue())
        }
        ModalBottomSheet(
            onDismiss,
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                Modifier
                    .then(if (searchable) Modifier.fillMaxHeight(0.6f) else Modifier)
            ) {
                Box(Modifier.padding(horizontal = 24.mdp)){
                    MyText(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.W500,
                        color = Color.Black
                    )
                }
                Spacer(Modifier.size(15.mdp))
                HorizontalDivider(color = Gray1)
                if (searchable){
                    InputTextField(
                        keyword,
                        {
                            keyword = it
                        },
                        label = "Search",
                        modifier = Modifier.padding(horizontal = 10.mdp),
                        leadingIcon = R.drawable.vuesax_linear_search_normal
                    )
                }
                Spacer(Modifier.size(10.mdp))
                list.filter { it.string().lowercase().contains(keyword.text.lowercase()) }.forEach {

                    Row(
                        Modifier.fillMaxWidth()
                            .clickable {
                                onSelect(it)
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    onDismiss()
                                }
                            }
                            .padding(vertical = 12.mdp, horizontal = 24.mdp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MyText(
                            text = it.string(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.W400,
                            color = Color.Black
                        )
                        if(selectedItem == it) {
                            Icon(
                                painter = painterResource(R.drawable.tick),
                                contentDescription = "",
                                tint = Primary,
                                modifier = Modifier.size(24.mdp)
                            )
                        }
                    }
                    HorizontalDivider(color = Gray1)
                }
            }
        }
    }
}