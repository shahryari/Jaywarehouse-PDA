package com.example.jaywarehouse.presentation.auth

import android.app.Activity
import android.app.Application
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.data.common.utils.mdp
import androidx.core.os.BuildCompat
import com.example.jaywarehouse.BuildConfig
import com.example.jaywarehouse.MainActivity
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.common.utils.restartActivity
import com.example.jaywarehouse.presentation.common.composables.BasicDialog
import com.example.jaywarehouse.presentation.common.composables.ErrorDialog
import com.example.jaywarehouse.presentation.common.composables.MyButton
import com.example.jaywarehouse.presentation.common.composables.MyCheckBox
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.MyTextField
import com.example.jaywarehouse.presentation.common.composables.SuccessToast
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.counting.contracts.CountingDetailContract
import com.example.jaywarehouse.presentation.destinations.LoginScreenDestination
import com.example.jaywarehouse.presentation.destinations.MainScreenDestination
import com.example.jaywarehouse.presentation.packing.DialogInput
import com.example.jaywarehouse.presentation.profile.ProfileContract
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Orange
import com.example.jaywarehouse.ui.theme.Red
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
                LoginContract.Effect.NavToMain -> navigator.navigate(MainScreenDestination,onlyIfResumed = true){
                    popUpTo(LoginScreenDestination){
                        inclusive = true
                    }
                }

                LoginContract.Effect.RestartActivity -> {
                    activity.restartActivity()
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
        if(state.toast.isNotEmpty()){
            delay(3000)
            onEvent(LoginContract.Event.HideToast)
        }
    }
    MyScaffold {
        Box(modifier = Modifier.fillMaxSize()){
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(10.mdp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.linari),
                        contentDescription = "",
                        Modifier.fillMaxWidth(0.7f),
                    )
                    Spacer(modifier = Modifier.size(40.mdp))
                    Box(modifier = Modifier.fillMaxWidth()){
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 10.mdp)
                                .matchParentSize()
                                .clip(RoundedCornerShape(10.mdp))
                                .background(Orange)
                        )
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 5.mdp)
                                .clip(RoundedCornerShape(10.mdp))
                                .background(Color.White)
                                .padding(15.mdp)
                        ) {
                            MyText(
                                text = stringResource(id = R.string.login),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(start = 7.mdp)
                            )
                            MyText(
                                text = stringResource(id = R.string.login_notice),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 7.mdp)
                            )
                            Spacer(modifier = Modifier.size(40.mdp))
                            TitleView(title = stringResource(id = R.string.username))
                            Spacer(modifier = Modifier.size(7.mdp))
                            MyTextField(
                                value = state.userName,
                                onValueChange = {
                                    onEvent(LoginContract.Event.OnUserNameChange(it))
                                },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = "",
                                        tint = Color.Gray
                                    )
                                },
                                trailingIcon = {
                                    if (state.userName.text.isNotEmpty()) {
                                        Icon(
                                            painterResource(id = R.drawable.vuesax_bulk_broom),
                                            contentDescription = "",
                                            tint = Black,
                                            modifier = Modifier
                                                .clickable {
                                                    onEvent(LoginContract.Event.OnUserNameChange(TextFieldValue()))
                                                }
                                        )
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.size(20.mdp))
                            TitleView(title = stringResource(id = R.string.password))
                            Spacer(modifier = Modifier.size(7.mdp))
                            MyTextField(
                                value = state.password,
                                onValueChange = {
                                    onEvent(LoginContract.Event.OnPasswordChange(it))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                maxLines = 1,
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.lock),
                                        contentDescription = "",
                                        tint = Black
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        painter = painterResource(id = if(!state.showPassword) R.drawable.vuesax_bulk_eye_slash else  R.drawable.vuesax_bulk_frame),
                                        contentDescription = "",
                                        tint = Black,
                                        modifier = Modifier.clickable {
                                            onEvent(LoginContract.Event.OnShowPassword(!state.showPassword))
                                        }
                                    )
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
                                        text = "Change Domain",
                                        color = Orange,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(modifier = Modifier.clickable {
                                    onEvent(LoginContract.Event.OnRememberMeChange(!state.rememberMe))
                                }, verticalAlignment = Alignment.CenterVertically) {

                                    MyCheckBox(checked = state.rememberMe) {
                                    }
                                    Spacer(modifier = Modifier.size(5.mdp))
                                    MyText(
                                        text = stringResource(id = R.string.remember_me),
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
                                title = stringResource(id = R.string.login),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                MyText(text = stringResource(id = R.string.version)+" ${BuildConfig.VERSION_NAME}")
            }
            SuccessToast(message = state.toast, modifier = Modifier.align(Alignment.TopCenter))

        }
    }
    if (state.error.isNotEmpty()){
        ErrorDialog(onDismiss = {
            onEvent(LoginContract.Event.CloseError)
        }, message = state.error)
    }
    if (state.showDomain){
        ChangeAddressDialog(state = state,onEvent)
    }
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
        title = "Change Address",
        positiveButton = "Save",
        negativeButton = "Cancel",
        onNegativeClick = {
            onEvent(LoginContract.Event.OnShowDomain(false))
        },
        onPositiveClick = {
            onEvent(LoginContract.Event.OnChangeDomain)
        }
    ){
        DialogInput(value = state.address,
            onValueChange = {
                onEvent(LoginContract.Event.OnAddressChange(it))
            },
            keyboardType = KeyboardType.Uri
        )
    }
}

@Composable
private fun TitleView(
    modifier: Modifier = Modifier,
    title: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .size(8.mdp)
                .clip(CircleShape)
                .background(Red)
        )
        Spacer(modifier = Modifier.size(15.mdp))
        MyText(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
private fun LoginPreview() {
    LoginContent()
}