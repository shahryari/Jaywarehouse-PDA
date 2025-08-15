package com.linari.presentation.auth

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.linari.MainActivity
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.data.common.utils.restartActivity
import com.linari.presentation.common.composables.BasicDialog
import com.linari.presentation.common.composables.ContainerBox
import com.linari.presentation.common.composables.ErrorDialog
import com.linari.presentation.common.composables.InputTextField
import com.linari.presentation.common.composables.MyButton
import com.linari.presentation.common.composables.MyCheckBox
import com.linari.presentation.common.composables.MyText
import com.linari.presentation.common.composables.MyTextField
import com.linari.presentation.common.composables.SuccessToast
import com.linari.presentation.common.composables.TitleView
import com.linari.presentation.common.utils.SIDE_EFFECT_KEY
import com.linari.presentation.dashboard.DashboardContract
import com.linari.presentation.destinations.DashboardScreenDestination
import com.linari.presentation.destinations.LoginScreenDestination
import com.linari.BuildConfig
import com.linari.presentation.common.utils.getLabelOf
import com.linari.ui.theme.Black
import com.linari.ui.theme.Gray3
import com.linari.ui.theme.Gray5
import com.linari.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@RootNavGraph(start = true)
@Destination
@Composable
fun LoginScreen(
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent
    val activity = LocalContext.current as MainActivity

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                LoginContract.Effect.NavToMain -> {
                    navigator.navigate(DashboardScreenDestination) {
                        popUpTo(LoginScreenDestination){
                            inclusive = true
                        }
                    }
                }

                LoginContract.Effect.RestartActivity -> {
                    activity.restartActivity()
                }
                is LoginContract.Effect.DownloadUpdate -> {
                    val intent = Intent(Intent.ACTION_VIEW, it.url.toUri())
                    activity.startActivity(intent)
                }

                LoginContract.Effect.CloseApp -> {
                    activity.finish()
                }

            }
        }
        
    }
    LoginContent(
        state,onEvent
    )
}

@Composable
private fun LoginContent(
    state: LoginContract.State = LoginContract.State(),
    onEvent: (LoginContract.Event)->Unit = {}
) {
    LaunchedEffect(key1 = state.toast) {
        if(state.toast!=null){
            delay(3000)
            onEvent(LoginContract.Event.HideToast)
        }
    }
    ContainerBox(showBackground = true) {
        Box(modifier = Modifier.fillMaxSize()){
            Image(
                painter = painterResource(id = R.drawable.linari),
                contentDescription = "",
                Modifier.padding(top = 42.mdp,start = 16.mdp).fillMaxWidth(0.6f).align(Alignment.TopStart),
                colorFilter = ColorFilter.tint(Color.White)
            )


            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .align(Alignment.BottomCenter)
                    .padding(top = 5.mdp)
                    .clip(RoundedCornerShape(topStart = 16.mdp, topEnd = 16.mdp))
                    .background(Color.White)
                    .padding(15.mdp)
            ) {
                MyText(
                    text = getLabelOf(stringResource(R.string.login)),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier.padding(start = 7.mdp)
                )
                MyText(
                    text = getLabelOf(stringResource(R.string.login_notice)),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier.padding(start = 7.mdp)
                )
                Spacer(modifier = Modifier.size(40.mdp))
                TitleView(title = getLabelOf(stringResource(id = R.string.username)))
                Spacer(modifier = Modifier.size(7.mdp))
                InputTextField(
                    value = state.userName,
                    onValueChange = {
                        onEvent(LoginContract.Event.OnUserNameChange(it))
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = R.drawable.user_svgrepo_com
                )
                Spacer(modifier = Modifier.size(20.mdp))
                TitleView(title = getLabelOf( stringResource(id = R.string.password)))
                Spacer(modifier = Modifier.size(7.mdp))
                InputTextField(
                    value = state.password,
                    onValueChange = {
                        onEvent(LoginContract.Event.OnPasswordChange(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    leadingIcon = R.drawable.lock,
                    trailingIcon = if(!state.showPassword) R.drawable.vuesax_bulk_eye_slash else  R.drawable.vuesax_bulk_frame,
                    showTrailingBorder = false,
                    onTrailingClick = {
                        onEvent(LoginContract.Event.OnShowPassword(!state.showPassword))
                    },
                    visualTransformation = if (state.showPassword) VisualTransformation.None else PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.size(7.mdp))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.clickable {
                        onEvent(LoginContract.Event.OnShowDomain(true))
                    }){

                        MyText(
                            text = getLabelOf(stringResource(R.string.change_domain)),
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(modifier = Modifier.clickable {
                        onEvent(LoginContract.Event.OnRememberMeChange(!state.rememberMe))
                    }, verticalAlignment = Alignment.CenterVertically) {

                        MyCheckBox(checked = state.rememberMe) {
                            onEvent(LoginContract.Event.OnRememberMeChange(!state.rememberMe))
                        }
                        Spacer(modifier = Modifier.size(5.mdp))
                        MyText(
                            text = getLabelOf(stringResource(id = R.string.remember_me)),
                            fontWeight = FontWeight.SemiBold,
                            color = Black.copy(0.5f)
                        )
                    }
                }
                Spacer(modifier = Modifier.size(40.mdp))
                MyButton(
                    onClick = {
                        onEvent(LoginContract.Event.OnLoginClick)
                    },
                    isLoading = state.isLoading,
                    title = getLabelOf(stringResource(id = R.string.login)),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            SuccessToast(message = state.toast?.let { stringResource(it) }?:"", modifier = Modifier.align(Alignment.TopCenter))

        }
    }
    if (state.error != null || state.serverError.isNotEmpty()){
        ErrorDialog(onDismiss = {
            onEvent(LoginContract.Event.CloseError)
        }, message = state.error?.let { stringResource(it) } ?: state.serverError)
    }
    if (state.showDomain){
        ChangeAddressDialog(state = state,onEvent)
    }
    DownloadVersionDialog(state = state, onEvent = onEvent)
}

@Composable
fun ChangeAddressDialog(
    state: LoginContract.State,
    onEvent: (LoginContract.Event) -> Unit
) {
    BasicDialog(
        onDismiss = {
            onEvent(LoginContract.Event.OnShowDomain(false))
        },
        title = getLabelOf(stringResource(R.string.change_domain)),
        positiveButton = getLabelOf(stringResource(R.string.save)),
        negativeButton = getLabelOf(stringResource(R.string.cancel)),
        onNegativeClick = {
            onEvent(LoginContract.Event.OnShowDomain(false))
        },
        onPositiveClick = {
            onEvent(LoginContract.Event.OnChangeDomain)
        }
    ){
        InputTextField(value = state.address,
            onValueChange = {
                onEvent(LoginContract.Event.OnAddressChange(it))
            },
            leadingContent = {
                Row {
                    Box(
                        Modifier.clip(RoundedCornerShape(6.mdp))
                            .background(if (state.domainPrefix == "https://") Primary else Color.White)
                            .clickable {
                                onEvent(LoginContract.Event.OnChangePrefix("https://"))
                            }
                            .padding(horizontal = 5.mdp, vertical = 3.mdp)
                    ) {
                        MyText(
                            text = "https://",
                            color = if (state.domainPrefix == "https://") Color.White else Primary,
                            fontSize = 12.sp
                        )
                    }
                    Box(
                        Modifier.clip(RoundedCornerShape(6.mdp))
                            .background(if (state.domainPrefix == "http://") Primary else Color.White)
                            .clickable {
                                onEvent(LoginContract.Event.OnChangePrefix("http://"))
                            }
                            .padding(horizontal = 5.mdp, vertical = 3.mdp)
                    ) {
                        MyText(
                            text = "http://",
                            color = if (state.domainPrefix == "http://") Color.White else Primary,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadVersionDialog(
    state: LoginContract.State = LoginContract.State(),
    onEvent: (LoginContract.Event)->Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true, confirmValueChange  = { false })
    val scope = rememberCoroutineScope()
    if (state.showUpdateDialog){
        ModalBottomSheet(
            onDismissRequest = { },
            sheetState = sheetState,
            properties = ModalBottomSheetProperties(false),
            containerColor = Color.White
        ) {
            Column(
                Modifier
                    .padding(horizontal = 24.mdp)
                    .padding(bottom = 24.mdp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MyText(
                    getLabelOf(stringResource(R.string.update_app_notice)),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W500,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Spacer(Modifier.size(10.mdp))
                MyText(
                    "${getLabelOf(stringResource(R.string.current_version))}: ${BuildConfig.VERSION_NAME}",
                    fontSize = 14.sp
                )
                Spacer(Modifier.size(5.mdp))
                MyText(
                    "${getLabelOf(stringResource(R.string.new_version))}: ${state.newVersion}",
                    fontSize = 14.sp
                )
                Spacer(Modifier.size(20.mdp))

                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            onEvent(LoginContract.Event.OnCloseApp)
                        },
                        title = getLabelOf(stringResource(R.string.close_app)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gray3,
                            contentColor = Gray5
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(10.mdp))
                    MyButton(
                        onClick = {

                            onEvent(LoginContract.Event.DownloadUpdate)
                        },
                        title = getLabelOf(stringResource(R.string.update)),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}



@Preview
@Composable
private fun LoginPreview() {
    LoginContent()
}