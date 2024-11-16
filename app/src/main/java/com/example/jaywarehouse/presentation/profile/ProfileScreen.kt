package com.example.jaywarehouse.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.common.utils.restartActivity
import com.example.jaywarehouse.presentation.common.composables.BasicDialog
import com.example.jaywarehouse.presentation.common.composables.ErrorDialog
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MySwitch
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.SuccessToast
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Gray2
import com.example.jaywarehouse.ui.theme.Green
import com.example.jaywarehouse.ui.theme.Red
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Destination(style = ScreenTransition::class)
@Composable
fun ProfileScreen(
    navigator: DestinationsNavigator,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent
    val context = LocalContext.current
    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                ProfileContract.Effect.NavBack -> {
                    navigator.popBackStack()
                }
                ProfileContract.Effect.RestartActivity -> {
                    context.restartActivity()
                }
            }
        }
    }
    ProfileContent(state, onEvent)
}

@Composable
fun ProfileContent(
    state: ProfileContract.State,
    onEvent: (ProfileContract.Event) -> Unit
) {
    LaunchedEffect(key1 = state.toast) {
        if(state.toast.isNotEmpty()){
            delay(3000)
            onEvent(ProfileContract.Event.HideToast)
        }
    }
    MyScaffold {
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp), verticalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.mdp))
                                .background(Color.Black.copy(0.85f))
                                .clickable {
                                    onEvent(ProfileContract.Event.OnNavBack)
                                }
                                .padding(5.mdp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "",
                                tint = Color.White,
                                modifier = Modifier.size(26.mdp)
                            )
                        }
                        Spacer(modifier = Modifier.size(10.mdp))
                        MyText(
                            "Profile",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.size(30.mdp))
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.avatar_user),
                                contentDescription ="",
                                Modifier.size(40.mdp)
                            )
                            Spacer(modifier = Modifier.size(12.mdp))
                            Column {
                                MyText(
                                    text = state.userFullName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Row (verticalAlignment = Alignment.CenterVertically){
                                    MyText(
                                        text = "Shams abad warehouse",
                                        color = MaterialTheme.colorScheme.outline
                                    )

                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(40.mdp))
                    MyText(
                        text = "Information",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.size(10.mdp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.mdp))
                            .background(Color.White)
                            .padding(8.mdp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.mdp))
                                    .background(Gray2)
                                    .padding(5.mdp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.vuesax_linear_global),
                                    contentDescription = "",
                                    tint = Black,
                                    modifier = Modifier
                                        .size(30.mdp)
                                )
                            }
                            Spacer(modifier = Modifier.size(10.mdp))
                            Column {
                                MyText(
                                    text = "Address",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.size(3.mdp))
                                MyText(
                                    text = state.address,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Normal,
                                    color = Black
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(20.mdp))
                    MyText(
                        text = "Preferences",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.size(10.mdp))
                    ProfileOption(
                        icon = R.drawable.category_2,
                        title = "Change Password",
                        onClick = {
                            onEvent(ProfileContract.Event.ShowChangePassword(true))
                        }
                    )
//                    Spacer(modifier = Modifier.size(10.mdp))
//                    ProfileOption(
//                        icon = R.drawable.vuesax_linear_global,
//                        title = "Address",
//                        onClick = {
//                            onEvent(ProfileContract.Event.ShowChangeAddress(true))
//                        }
//                    )

                    Spacer(modifier = Modifier.size(10.mdp))
                    ProfileOption(
                        icon = R.drawable.vuesax_bold_home_2,
                        title = "Forward To Dashboard",
                        description = "Navigate to dashboard after scan all items",
                        switchable = true,
                        switch = state.isNavToParent,
                        onClick = {
                            onEvent(ProfileContract.Event.OnNavToParentChange(!state.isNavToParent))
                        }
                    )
                    Spacer(modifier = Modifier.size(10.mdp))
                    ProfileOption(
                        icon = R.drawable.note,
                        title = "Open Detail",
                        description = "Navigate to detail screen after search",
                        switchable = true,
                        switch = state.isNavToDetail,
                        onClick = {
                            onEvent(ProfileContract.Event.OnNavToDetailChange(!state.isNavToDetail))
                        }
                    )
                    Spacer(modifier = Modifier.size(10.mdp))
                    ProfileOption(
                        icon = R.drawable.barcode,
                        title = "Lock KeyBoard",
                        switchable = true,
                        switch = state.lockKeyboard,
                        onClick = {
                            onEvent(ProfileContract.Event.OnLockKeyboardChange(!state.lockKeyboard))
                        }
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.mdp))
                        .background(Color.White)
                        .clickable {
                            onEvent(ProfileContract.Event.OnLogout)
                        }
                        .padding(8.mdp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.mdp))
                            .background(Red.copy(0.1f))
                            .padding(5.mdp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.vuesax_linear_logout),
                            contentDescription = "",
                            tint = Red,
                            modifier = Modifier
                                .size(30.mdp)
                        )
                    }
                    Spacer(modifier = Modifier.size(10.mdp))
                    MyText(
                        text = "Logout",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Red
                    )
                }

            }

            SuccessToast(
                Modifier
                    .padding(12.mdp)
                    .align(alignment = Alignment.TopCenter),message = state.toast)
        }
    }
    if (state.showChangeAddress) ChangeAddressDialog(state = state, onEvent = onEvent)
    if (state.showChangePassword) ChangePasswordDialog(state = state, onEvent = onEvent)
    if (state.error.isNotEmpty()) ErrorDialog(onDismiss = { onEvent(ProfileContract.Event.CloseError) }, message = state.error)
}

@Composable
fun ChangeAddressDialog(
    state: ProfileContract.State,
    onEvent: (ProfileContract.Event) -> Unit
) {
    BasicDialog(
        onDismiss = {
            onEvent(ProfileContract.Event.ShowChangeAddress(false))
        },
        positiveButton = "Dismiss",
        onPositiveClick = {
            onEvent(ProfileContract.Event.ShowChangeAddress(false))
        }
    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.vuesax_linear_global),
                contentDescription = "",
                tint = Color.White.copy(0.7f),
                modifier = Modifier.size(100.mdp)
            )
            Spacer(modifier = Modifier.size(15.mdp))
            MyText(
                text = state.address,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.size(30.mdp))
        }
    }
}

@Composable
fun ChangePasswordDialog(
    state: ProfileContract.State,
    onEvent: (ProfileContract.Event) -> Unit
) {
    BasicDialog(
        onDismiss = {
            onEvent(ProfileContract.Event.ShowChangePassword(false))
        },
        title = "Change Password",
        positiveButton = "Save",
        negativeButton = "Cancel",
        onNegativeClick = {
            onEvent(ProfileContract.Event.ShowChangePassword(false))
        },
        isLoading = state.isLoading,
        onPositiveClick = {
            onEvent(ProfileContract.Event.OnSubmitPassword)
        }
    ){
//        DialogInput(value = state.password,
//            onValueChange = {
//                onEvent(ProfileContract.Event.OnPasswordChange(it))
//            },
//            keyboardType = KeyboardType.Password,
//            icon = R.drawable.lock
//        )
    }
}

@Composable
fun ProfileOption(
    modifier: Modifier = Modifier,
    icon: Int,
    title: String,
    onClick: ()->Unit,
    label: String = "",
    description: String = "",
    switchable: Boolean = false,
    switch: Boolean = false,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.mdp))
            .background(Color.White)
            .clickable {
                onClick()
            }
            .padding(8.mdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.mdp))
                    .background(Gray2)
                    .padding(5.mdp)
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = "",
                    tint = Black,
                    modifier = Modifier
                        .size(30.mdp)
                )
            }
            Spacer(modifier = Modifier.size(10.mdp))
            Column {

                MyText(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.outline
                )
                if (description.isNotEmpty())MyText(
                    text = description,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline.copy(0.8f),
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.size(10.mdp))
            if (label.isNotEmpty())MyText(
                text = "300",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Green)
                    .padding(5.mdp),
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        if (switchable)MySwitch(active = switch) {
            onClick()
        } else {
            Icon(
                painterResource(id = R.drawable.arrow_right),
                contentDescription = "",
                tint = Color.Black,
                modifier = Modifier
                    .size(30.mdp)
            )
        }

    }
}

@Preview
@Composable
private fun ProfilePreview() {
    MyScaffold {
        ProfileContent(ProfileContract.State(address = "www.google.com"),{})
    }
}