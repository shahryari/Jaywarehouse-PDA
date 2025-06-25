package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.presentation.common.utils.Selectable
import com.example.jaywarehouse.ui.theme.Border

@Composable
fun <T: Selectable>ComboBox(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedItem: T?,
    fillMaxWith: Boolean = true,
    widthFraction: Float = 1f,
    icon: Int? = null,
    listPadding: PaddingValues = PaddingValues(horizontal = 15.mdp),
    onSelectItem: (T)-> Unit
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    Column(modifier
        .then(if(fillMaxWith) Modifier.fillMaxWidth() else Modifier)
        .clickable {
            isExpanded = !isExpanded
        }
    ) {
        Row(
            Modifier
                .then(if (fillMaxWith) Modifier.fillMaxWidth() else Modifier.fillMaxWidth(0.3f))
                .clip(RoundedCornerShape(6.mdp))
                .background(color = Color.Transparent)
                .border(1.mdp, Border,RoundedCornerShape(6.mdp))
                .padding(vertical = 9.mdp, horizontal = 10.mdp),
            verticalAlignment = Alignment.CenterVertically
        ){

            if (icon != null) {
                MyIcon(icon = icon, showBorder = false, clickable = false)
                Spacer(modifier = Modifier.size(7.mdp))
            }
            MyText(
                selectedItem?.string()?:"",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
        Box {
            if (isExpanded){
                Popup(
                    alignment = Alignment.TopStart,
                    onDismissRequest = {
                        isExpanded = false
                    },
                ) {
                    Column(Modifier.padding(listPadding).shadow(2.mdp)) {
                        for ((i,item) in items.withIndex()){
                            Column {
                                Box(
                                    Modifier
                                        .then(if (fillMaxWith) Modifier.fillMaxWidth(widthFraction) else Modifier.fillMaxWidth(0.3f))
                                        .background(Color.White)
                                        .clickable {
                                            onSelectItem(item)
                                            isExpanded = false
                                        }
                                        .padding(vertical = 9.mdp, horizontal = 10.mdp)
                                ){
                                    MyText(
                                        item.string(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}