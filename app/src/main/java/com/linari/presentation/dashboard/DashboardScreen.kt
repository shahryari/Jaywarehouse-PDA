package com.linari.presentation.dashboard

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.linari.MainActivity
import com.linari.data.common.utils.mdp
import com.linari.R
import com.linari.data.auth.models.AccessPermissionModel
import com.linari.data.auth.models.DashboardModel
import com.linari.data.auth.models.WarehouseModel
import com.linari.data.common.utils.restartActivity
import com.linari.presentation.common.composables.ComboBox
import com.linari.presentation.common.composables.MyButton
import com.linari.presentation.common.composables.MyScaffold
import com.linari.presentation.common.composables.MySwitch
import com.linari.presentation.common.composables.MyText
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.MainItems
import com.linari.presentation.common.utils.SIDE_EFFECT_KEY
import com.linari.presentation.common.utils.ScreenTransition
import com.linari.presentation.picking.contracts.PickingListBDContract
import com.linari.ui.theme.Black
import com.linari.ui.theme.Border
import com.linari.ui.theme.ErrorRed
import com.linari.ui.theme.Gray1
import com.linari.ui.theme.Gray3
import com.linari.ui.theme.Gray4
import com.linari.ui.theme.Gray5
import com.linari.ui.theme.Primary
import com.linari.ui.theme.Red
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.collections.iterator
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.linari.BuildConfig
import com.linari.data.common.utils.createImageUri
import com.linari.data.common.utils.getFileFromUri
import com.linari.presentation.auth.LoginContract
import com.linari.presentation.common.composables.InputTextField
import com.linari.presentation.common.composables.TitleView

@Destination(style = ScreenTransition::class)
@Composable
fun DashboardScreen(
    navigator: DestinationsNavigator,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent
    val activity = LocalContext.current as MainActivity
    var image by remember {
        mutableStateOf<Uri?>(null)
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {image->
        if(image == null){
            return@rememberLauncherForActivityResult
        }
        onEvent(DashboardContract.Event.ChangeProfile(getFileFromUri(activity,image)))
    }



    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {output->
        if(output && image != null){

            onEvent(DashboardContract.Event.ChangeProfile(getFileFromUri(activity,image!!)))
        }
    }

    val cameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it){
            image = createImageUri(activity,"profile_${System.currentTimeMillis()}")
            cameraLauncher.launch(image!!)
        }
    }
    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                is DashboardContract.Effect.Navigate -> navigator.navigate(it.destination)
                DashboardContract.Effect.RestartActivity -> {
                    activity.restartActivity()
                }

                is DashboardContract.Effect.DownloadUpdate -> {
                    val intent = Intent(Intent.ACTION_VIEW, it.url.toUri())
                    activity.startActivity(intent)
                }

                DashboardContract.Effect.CloseApp -> {
                    activity.finish()
                }

                DashboardContract.Effect.OpenCamera -> {
                    cameraPermission.launch(Manifest.permission.CAMERA)
                }
                DashboardContract.Effect.OpenGallery -> {
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }
        }

    }

    DashboardContent(state,onEvent = onEvent)
}



@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun DashboardContent(
    state: DashboardContract.State = DashboardContract.State(),
    onEvent: (DashboardContract.Event)->Unit = {}
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        onEvent(DashboardContract.Event.FetchData())
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(DashboardContract.Event.CloseError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(DashboardContract.Event.CloseToast)
        },
        onRefresh = {
            onEvent(DashboardContract.Event.FetchData(Loading.REFRESHING))
        },
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(drawerShape = RectangleShape, drawerContainerColor = Color.White) {
                    Column(
                        Modifier
                            .fillMaxWidth(0.8f)
                            .padding(12.mdp)) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .shadow(5.mdp, RoundedCornerShape(8.mdp))
                                .clip(RoundedCornerShape(8.mdp))
                                .background(Color.White)
                                .padding(8.mdp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(8.mdp))
                                    .clickable {
                                        onEvent(DashboardContract.Event.ShowChangeProfileDialog(true))
                                    }
                            ){

                                val context = LocalContext.current

                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .addHeader("Cookie",state.cookie)
                                        .data(state.savedProfile)
                                        .build(),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(40.mdp)
                                        .clip(RoundedCornerShape(8.mdp)),
                                    contentScale = ContentScale.Crop,
                                    error = painterResource(R.drawable.user_prof),
                                    onError = {
                                        Log.e("profile_image", "DashboardContent: ", it.result.throwable)
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.size(12.mdp))
                            MyText(
                                text = state.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.W500,
                                color = Primary
                            )
                        }
                        Spacer(modifier = Modifier.size(15.mdp))
                        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                            AnimatedContent(state.subDrawerState, label = "drawer Animation") { subState ->
                                when(subState){
                                    SubDrawerState.Drawers -> {
                                        LazyColumn(Modifier.fillMaxWidth()) {
                                            item("Home"){

                                                DrawerItem(
                                                    title = "Home",
                                                    icon = R.drawable.home_02,
                                                    selected = true,
                                                    modifier = Modifier.animateEnterExit(),
                                                    onClick = {

                                                    }
                                                )
                                            }
                                            items(state.dashboards.toList()){
                                                val details = it.second.filter {item->
                                                    state.accessPermissions?.checkAccess(item) == true
                                                }
                                                if (details.isNotEmpty()) {
                                                    DrawerItem(
                                                        title = it.first,
                                                        icon = when(it.first){
                                                            "Count"-> R.drawable.search
                                                            "Shipping" -> R.drawable.truck_next
                                                            "Stock" -> R.drawable.inventory
                                                            else -> R.drawable.integeration
                                                        },
                                                        modifier = Modifier.animateEnterExit(),
                                                        onClick = {
                                                            onEvent(DashboardContract.Event.OnShowSubDrawers(it.second))
                                                        }
                                                    )
                                                }
                                            }
//                                            if (state.selectedTab == DashboardTab.Picking){
//                                            } else {
//                                                items(state.crossDockDashboards.values.first()){
//                                                    DrawerItem(
//                                                        it.title.replace('\n',' '),
//                                                        it.icon,
//                                                        modifier = Modifier.animateEnterExit(
//                                                            enter = slideInHorizontally(),
//                                                            exit = slideOutHorizontally()
//                                                        ),
//                                                    ) {
//                                                        when(it){
//                                                            MainItems.Receiving -> {
//                                                                onEvent(DashboardContract.Event.OnNavigate(
//                                                                    CountingScreenDestination(isCrossDock = true)))
//                                                            }
//                                                           else -> {}
//                                                        }
//                                                    }
//                                                }
//                                            }
                                        }
                                    }
                                    SubDrawerState.SubDrawers -> {
                                        if (state.subDrawers!=null)LazyColumn(Modifier.fillMaxWidth()) {
                                            item(key = "Main Menu") {
                                                DrawerItem(
                                                    title = "Main Menu",
                                                    icon = R.drawable.category_2,
                                                    selected = true,
                                                    modifier = Modifier.animateEnterExit(),
                                                    onClick = {
                                                        onEvent(DashboardContract.Event.OnShowSubDrawers(null))
                                                    }
                                                )
                                            }
                                            items(state.subDrawers.filter {
                                                state.accessPermissions?.checkAccess(it) == true
                                            }){
                                                DrawerItem(
                                                    title = it.title.replace('\n',' '),
                                                    icon = it.icon,
                                                    modifier = Modifier.animateEnterExit(
                                                        enter = slideInHorizontally(),
                                                        exit = slideOutHorizontally()
                                                    ),
                                                    onClick = {
                                                        if (it.destination!=null){
                                                            onEvent(DashboardContract.Event.OnNavigate(it.destination))
                                                        }
                                                        onEvent(DashboardContract.Event.OnShowSubDrawers(null))
                                                    }
                                                )
                                            }
                                        }

                                    }
                                    SubDrawerState.Settings -> {
                                        Column(Modifier.verticalScroll(rememberScrollState())) {
                                            DrawerItem(
                                                title = "Main Menu",
                                                icon = R.drawable.category_2,
                                                selected = true,
                                                modifier = Modifier.animateEnterExit(),
                                                onClick = {
                                                    onEvent(DashboardContract.Event.ShowSettings(false))
                                                }
                                            )
                                            HorizontalDivider(color = Gray4)
                                            SettingDrawer(
                                                title = "Change password",
                                                icon = R.drawable.unlock_3,
                                                showSwitch = false,
                                                onClick = {
                                                    onEvent(DashboardContract.Event.OnShowChangePasswordDialog(true))
                                                }
                                            )
                                            HorizontalDivider(color = Gray4)
                                            SettingDrawer(
                                                title = "Change Profile",
                                                icon = R.drawable.profile_2user,
                                                showSwitch = false,
                                                onClick = {
                                                    onEvent(DashboardContract.Event.ShowChangeProfileDialog(true))
                                                }
                                            )
                                            HorizontalDivider(color = Gray4)
                                            SettingDrawer(
                                                title = "Forward ot dashboard",
                                                icon = R.drawable.home_02,
                                                showSwitch = true,
                                                switchState = state.forwardToDashboard,
                                                onSwitchChange = {
                                                    onEvent(DashboardContract.Event.OnForwardToDashboard(it))
                                                }
                                            )
                                            HorizontalDivider(color = Gray4)
                                            SettingDrawer(
                                                title = "Open detail",
                                                icon = R.drawable.notes,
                                                showSwitch = true,
                                                switchState = state.openDetail,
                                                onSwitchChange = {
                                                    onEvent(DashboardContract.Event.OnOpenDetail(it))
                                                }
                                            )
                                            HorizontalDivider(color = Gray4)
                                            SettingDrawer(
                                                title = "Enable Add Extra In Cycle Count",
                                                icon = R.drawable.add_square,
                                                showSwitch = true,
                                                switchState = state.addExtraCycle,
                                                onSwitchChange = {
                                                    onEvent(DashboardContract.Event.OnAddExtraCycleChange(it))
                                                }
                                            )
                                            HorizontalDivider(color = Gray4)
                                            SettingDrawer(
                                                title = "Validate Pallet Number",
                                                icon = R.drawable.barcode,
                                                showSwitch = true,
                                                switchState = state.validatePallet,
                                                onSwitchChange = {
                                                    onEvent(DashboardContract.Event.OnValidatePalletChange(it))
                                                }
                                            )
                                            HorizontalDivider(color = Gray4)
                                            SettingDrawer(
                                                title = "Enable Tracking",
                                                icon = R.drawable.location,
                                                showSwitch = true,
                                                switchState = state.enableTracking,
                                                onSwitchChange = {
                                                    onEvent(DashboardContract.Event.EnableTracking(it))
                                                }
                                            )
                                            HorizontalDivider(color = Gray4)
                                            SettingDrawer(
                                                title = "Reopen on Incomplete Check",
                                                icon = R.drawable.vuesax_outline_box_tick,
                                                showSwitch = true,
                                                switchState = state.enableAutoOpenChecking,
                                                onSwitchChange = {
                                                    onEvent(DashboardContract.Event.OnEnableAutoOpenChecking(it))
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                SettingDrawer(
                                    title = "Lock keyboard",
                                    icon = R.drawable.keyboard,
                                    showSwitch = true,
                                    switchState = state.lockKeyboard,
                                    onSwitchChange = {
                                        onEvent(DashboardContract.Event.OnLockKeyboardChange(it))
                                    }
                                )
                                HorizontalDivider(color = Gray4)
                                Row(
                                    Modifier
                                        .padding(top = 5.mdp, bottom = 2.mdp)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.mdp))
                                        .background(Color.White)
                                        .clickable {
                                            onEvent(DashboardContract.Event.ShowSettings(true))
                                        }
                                        .padding(12.mdp),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Outlined.Settings,
                                        contentDescription = "",
                                        modifier = Modifier.size(24.mdp),
                                        tint = Black
                                    )
                                    Spacer(Modifier.size(20.mdp))
                                    MyText(
                                        "Settings",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.W500,
                                        color = Black
                                    )
                                }
                                Row(
                                    Modifier
                                        .padding(top = 5.mdp, bottom = 2.mdp)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.mdp))
                                        .background(Color.White)
                                        .border(1.mdp, ErrorRed, RoundedCornerShape(6.mdp))
                                        .clickable {
                                            onEvent(DashboardContract.Event.OnLogout)
                                        }
                                        .padding(12.mdp),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painterResource(R.drawable.logout_01),
                                        contentDescription = "",
                                        modifier = Modifier.size(24.mdp),
                                        tint = ErrorRed
                                    )
                                    Spacer(Modifier.size(20.mdp))
                                    MyText(
                                        "Logout",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.W500,
                                        color = ErrorRed
                                    )
                                }
                                Spacer(Modifier.size(10.mdp))
                                MyText(
                                    "Version ${BuildConfig.VERSION_NAME}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        ){
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(1.mdp, RoundedCornerShape(6.mdp))
                            .clip(RoundedCornerShape(6.mdp))
                            .background(Color.White)
                            .clickable {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                            .padding(12.mdp)
                    ) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "",
                            modifier = Modifier.size(24.mdp),
                            tint = Black
                        )
                    }
                    Box(
                        modifier = Modifier
                            .shadow(1.mdp, RoundedCornerShape(6.mdp))
                            .clip(RoundedCornerShape(6.mdp))
                            .clickable {
                                onEvent(DashboardContract.Event.OnShowWarehouseList(true))
                            }
                            .background(Color.White)
                            .padding(13.mdp)
                    ) {
                        MyText(
                            "${state.selectedWarehouse?.name?:""}(${state.selectedWarehouse?.code?:""})",
                            fontWeight = FontWeight.W500,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(Modifier.size(10.mdp))
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Spacer(Modifier.size(10.mdp))
                    state.dashboards.forEach {entry ->
                        Column {
                            val labelVisible = when(entry.key){
                                "Count"->{
                                    true
                                }
                                "Shipping"->{
                                    state.dashboardsVisibility.filter{it.key.category == "Count"}.all { it.value }
                                }
                                "Stock"->{
                                    state.dashboardsVisibility.filter{it.key.category == "Shipping"}.all { it.value }

                                }
                                "Integration"-> {
                                    state.dashboardsVisibility.filter{it.key.category == "Stock"}.all { it.value }

                                }
                                else -> {
                                    true
                                }
                            }
//                                val labelVisible = !state.dashboardsVisibility.any { it.key.category == entry.key && !it.value }
                            DashboardListItem(entry,labelVisible,state.dashboardsVisibility,state.dashboard,state.accessPermissions) {
                                if(it.destination!=null)onEvent(DashboardContract.Event.OnNavigate(it.destination))
                            }
                        }
                        Spacer(modifier = Modifier.size(15.mdp))
                    }
                }
            }
        }
    }

    if (state.showWarehouseList){
        WarehouseListSheet(
            onDismiss = {
                onEvent(DashboardContract.Event.OnShowWarehouseList(false))
            },
            warehouseList = state.warehouseList,
            selectedWarehouse = state.selectedWarehouse
        ) {
            onEvent(DashboardContract.Event.OnSelectWarehouse(it))
        }
    }
    DownloadVersionDialog(state,onEvent)
    ChangePasswordSheet(state,onEvent)
    ChangeProfileSheet(state,onEvent)
}

@Composable
fun SettingDrawer(
    modifier: Modifier = Modifier,
    title: String,
    icon: Int,
    switchState: Boolean = false,
    showSwitch: Boolean = true,
    onClick: (() -> Unit)? = null,
    onSwitchChange: (Boolean)->Unit = {}
) {
    Row(
        modifier
            .padding(top = 5.mdp, bottom = 2.mdp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.mdp))
            .background(Color.White)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .padding(12.mdp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painterResource(icon),
                contentDescription = "",
                modifier = Modifier.size(24.mdp),
                tint = Black
            )
            Spacer(Modifier.size(20.mdp))
            MyText(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.W500,
                color = Black
            )
        }
        Spacer(Modifier.size(5.mdp))
        if (showSwitch) MySwitch(switchState) {
            onSwitchChange(it)
        } else {
            Icon(
                painterResource(R.drawable.direction_right_2),
                contentDescription = "",
                modifier = Modifier.size(24.mdp),
                tint = Black
            )
        }
    }
}

@Composable
fun DrawerItem(
    title: String,
    icon: Int,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isPressed by interactionSource.collectIsPressedAsState()

    Row(
        modifier
            .padding(top = 5.mdp, bottom = 2.mdp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.mdp))
            .background(if (selected) Primary.copy(0.2f) else Color.White)
            .clickable(
                indication = ripple(color = Primary),
                interactionSource = interactionSource,
                onClick = onClick
            )
            .padding(12.mdp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painterResource(icon),
            contentDescription = "",
            modifier = Modifier.size(24.mdp),
            tint = if (selected) Primary else Black
        )
        Spacer(Modifier.size(20.mdp))
        MyText(
            title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.W500,
            color = if (selected) Primary else Black
        )
    }
    HorizontalDivider(
        color = if (!selected) Gray4 else Color.White
    )
}

@Composable
fun DashboardListItem(
    item: Map.Entry<String, List<MainItems>>,
    labelVisibility: Boolean = false,
    visibility: Map<MainItems, Boolean>,
    dashboardModel: DashboardModel?,
    access: AccessPermissionModel?,
    onItemClick: (MainItems)-> Unit
) {
    val list = item.value.filter { access?.checkAccess(it) == true  }

    if (list.isNotEmpty()) {
        AnimatedVisibility(labelVisibility) {
            MyText(
                item.key,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W500,
                fontSize = 16.sp,
                color = Black
            )
        }
        Spacer(Modifier.size(5.mdp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            for (i in 0..3) {
                val it = list.getOrNull(i)
                if (it != null) {

                    DashboardSubItem(
                        item = it,
                        count = dashboardModel?.getCount(it),
                        isVisible = visibility.getOrDefault(it, false),
                        onClick = {
                            onItemClick(it)
                        }
                    )
                } else {
                    Spacer(Modifier.size(80.mdp))
                }
            }
        }
        if (list.size > 4) Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 4..7) {
                val it = list.getOrNull(i)
                if (it != null) {
                    DashboardSubItem(
                        item = it,
                        count = dashboardModel?.getCount(it),
                        isVisible = visibility.getOrDefault(it, false),
                        onClick = {
                            onItemClick(it)
                        }
                    )
                } else {
                    Spacer(Modifier.size(80.mdp))
                }
            }
        }
    }
}

@Composable
fun DashboardSubItem(
    item: MainItems,
    count: Int? = null,
    isVisible: Boolean = false,
    onClick: () -> Unit
) {

    AnimatedVisibility (
        isVisible,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box {

                Box(
                    Modifier
                        .padding(10.mdp)
                        .clip(RoundedCornerShape(6.mdp))
                        .background(item.color)
                        .clickable {
                            onClick()
                        }
                        .padding(14.mdp)
                ) {
                    Icon(
                        painterResource(item.icon),
                        contentDescription = "",
                        modifier = Modifier.size(32.mdp),
                        tint = Color.White
                    )
                }
                if (count!=null && count > 0) {
                    Box(
                        Modifier
                            .padding(3.mdp)
                            .align(Alignment.TopEnd)
                            .clip(RoundedCornerShape(4.mdp))
                            .background(Red)
                            .padding(vertical = 2.mdp, horizontal = 8.mdp)
                    ) {
                        MyText(
                            count.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.W500,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
//                if (showDot){
//                    Box(
//                        Modifier
//                            .padding(2.mdp)
//                            .align(Alignment.TopEnd)
//                            .clip(CircleShape)
//                            .border(1.mdp, Red.copy(0.45f), CircleShape)
//                            .padding(3.mdp)
//                    ){
//                        Box(
//                            Modifier
//                                .size(14.mdp)
//                                .clip(CircleShape)
//                                .background(Red)
//                        )
//                    }
//                }
            }
            Spacer(Modifier.size(5.mdp))
            MyText(
                item.title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.W500,
                textAlign = TextAlign.Center,
                color = Black
            )
        }
    }
    if (!isVisible) Spacer(Modifier.size(height = 100.mdp,width = 78.mdp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarehouseListSheet(
    onDismiss: ()-> Unit,
    warehouseList: List<WarehouseModel>,
    selectedWarehouse: WarehouseModel?,
    onSelectWarehouse:(WarehouseModel)-> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
        ) {
            Box(Modifier.padding(horizontal = 24.mdp)){
                MyText(
                    "Warehouse List",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.W500,
                    color = Color.Black
                )
            }
            Spacer(Modifier.size(15.mdp))
            HorizontalDivider(color = Gray1)

            for (warehouse in warehouseList) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectWarehouse(warehouse)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onDismiss()
                            }
                        }
                        .padding(vertical = 12.mdp, horizontal = 24.mdp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MyText(
                        text = "${warehouse.name}(${warehouse.code})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W400,
                        color = Color.Black
                    )
                    if(selectedWarehouse == warehouse) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordSheet(
    state: DashboardContract.State = DashboardContract.State(),
    onEvent: (DashboardContract.Event)->Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    if (state.showChangePasswordDialog){
        ModalBottomSheet(
            onDismissRequest = {
                onEvent(DashboardContract.Event.OnShowChangePasswordDialog(false))
            },
            sheetState = sheetState,
            properties = ModalBottomSheetProperties(false),
            containerColor = Color.White
        ) {
            Column(
                Modifier
                    .padding(horizontal = 24.mdp)
                    .padding(bottom = 24.mdp),
            ) {
                MyText(
                    text = "Change Password",
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.size(15.mdp))
                TitleView(
                    title= "Old Password"
                )
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    value = state.oldPassword,
                    onValueChange = {
                        onEvent(DashboardContract.Event.OnChangeOldPassword(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    leadingIcon = R.drawable.lock,
                    trailingIcon = if(!state.showOldPassword) R.drawable.vuesax_bulk_eye_slash else  R.drawable.vuesax_bulk_frame,
                    showTrailingBorder = false,
                    onTrailingClick = {
                        onEvent(DashboardContract.Event.OnShowOldPassword(!state.showOldPassword))
                    },
                    visualTransformation = if (state.showOldPassword) VisualTransformation.None else PasswordVisualTransformation()
                )
                Spacer(Modifier.size(10.mdp))
                TitleView(
                    title = "New Password"
                )
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    value = state.password,
                    onValueChange = {
                        onEvent(DashboardContract.Event.OnChangePassword(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    leadingIcon = R.drawable.lock,
                    trailingIcon = if(!state.showPassword) R.drawable.vuesax_bulk_eye_slash else  R.drawable.vuesax_bulk_frame,
                    showTrailingBorder = false,
                    onTrailingClick = {
                        onEvent(DashboardContract.Event.OnShowPassword(!state.showPassword))
                    },
                    visualTransformation = if (state.showPassword) VisualTransformation.None else PasswordVisualTransformation()
                )
                Spacer(Modifier.size(10.mdp))
                TitleView(
                    title = "Confirm New Password"
                )
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    value = state.confirmPassword,
                    onValueChange = {
                        onEvent(DashboardContract.Event.OnChangeConfirmPassword(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    leadingIcon = R.drawable.lock,
                    trailingIcon = if(!state.showConfirmPassword) R.drawable.vuesax_bulk_eye_slash else  R.drawable.vuesax_bulk_frame,
                    showTrailingBorder = false,
                    onTrailingClick = {
                        onEvent(DashboardContract.Event.OnShowConfirmPassword(!state.showConfirmPassword))
                    },
                    visualTransformation = if (state.showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation()
                )
                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            onEvent(DashboardContract.Event.OnShowChangePasswordDialog(false))
                        },
                        title = "Cancel",
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gray3,
                            contentColor = Gray5
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(10.mdp))
                    MyButton(
                        onClick = {

                            onEvent(DashboardContract.Event.ChangePassword)
                        },
                        title = "Change",
                        enabled = state.oldPassword.text.isNotEmpty() && state.password.text.isNotEmpty() && state.confirmPassword.text.isNotEmpty(),
                        isLoading = state.isChangingPassword,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadVersionDialog(
    state: DashboardContract.State = DashboardContract.State(),
    onEvent: (DashboardContract.Event)->Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    if (state.showUpdateDialog){
        ModalBottomSheet(
            onDismissRequest = {
                onEvent(DashboardContract.Event.ShowDownloadUpdate(false))
            },
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
                    "Please Update your app, a newer version is available",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W500,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Spacer(Modifier.size(10.mdp))
                MyText(
                    "Current Version: ${BuildConfig.VERSION_NAME}",
                    fontSize = 14.sp
                )
                Spacer(Modifier.size(5.mdp))
                MyText(
                    "New Version: ${state.newVersion}",
                    fontSize = 14.sp
                )
                Spacer(Modifier.size(20.mdp))

                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            onEvent(DashboardContract.Event.OnCloseApp)
                        },
                        title = "Close App",
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gray3,
                            contentColor = Gray5
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(10.mdp))
                    MyButton(
                        onClick = {

                            onEvent(DashboardContract.Event.DownloadUpdate)
                        },
                        title = "Update",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeProfileSheet(
    state: DashboardContract.State = DashboardContract.State(),
    onEvent: (DashboardContract.Event)->Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (state.showChangeProfileDialog){
        ModalBottomSheet(
            onDismissRequest = {
                onEvent(DashboardContract.Event.ShowChangeProfileDialog(false))
            },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                Modifier
                    .padding(horizontal = 24.mdp)
                    .padding(bottom = 24.mdp)
            ) {
                MyText(
                    text = "Change Profile",
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.size(15.mdp))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.mdp))
                        .border(1.mdp, Border, RoundedCornerShape(6.mdp))
                        .padding(15.mdp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    if(state.profile!=null){
                        AsyncImage(
                            state.profile,
                            contentDescription = "",
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.mdp))
                                .fillMaxWidth()
                                .aspectRatio(2f),
                            placeholder = painterResource(R.drawable.ic_picture),
                            error = painterResource(R.drawable.ic_picture),
                        )
                    } else {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .aspectRatio(2f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_picture),
                                contentDescription = "",
                                modifier = Modifier.size(35.mdp),
                                tint = Color.Gray
                            )
                            Spacer(Modifier.size(5.mdp))
                            MyText(
                                "Please select your profile picture",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Spacer(Modifier.size(5.mdp))

                    if (state.profile==null) Row(Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                onEvent(DashboardContract.Event.OnOpenGallery)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(6.mdp),
                            modifier = Modifier.weight(1f)
                        ) {
                            MyText(
                                "Gallery",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                        Spacer(Modifier.size(5.mdp))
                        Button(
                            onClick = {
                                onEvent(DashboardContract.Event.OnOpenCamera)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(6.mdp),
                            modifier = Modifier.weight(1f)
                        ) {
                            MyText(
                                "Camera",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    } else{
                        Button(
                            onClick = {
                                onEvent(DashboardContract.Event.ChangeProfile(null))
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            ),
                            shape = RoundedCornerShape(6.mdp),
                        ) {
                            MyText(
                                "Delete",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            onEvent(DashboardContract.Event.ShowChangeProfileDialog(false))
                        },
                        title = "Cancel",
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gray3,
                            contentColor = Gray5
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(10.mdp))
                    MyButton(
                        onClick = {

                            onEvent(DashboardContract.Event.SaveProfile)
                        },
                        title = "Save",
                        isLoading = state.isSavingProfile,
                        enabled = state.profile!=null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DashboardPreview() {
    DashboardContent()
}