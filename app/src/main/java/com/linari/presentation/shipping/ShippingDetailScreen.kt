package com.linari.presentation.shipping

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.data.common.utils.removeZeroDecimal
import com.linari.data.pallet.model.PalletManifestProductRow
import com.linari.data.picking.models.PalletManifest
import com.linari.data.shipping.models.PalletMaskModel
import com.linari.data.shipping.models.ShippingDetailListOfPalletRow
import com.linari.data.shipping.models.ShippingPalletManifestRow
import com.linari.data.shipping.models.ShippingRow
import com.linari.presentation.common.composables.BaseListItem
import com.linari.presentation.common.composables.BaseListItemModel
import com.linari.presentation.common.composables.DetailCard
import com.linari.presentation.common.composables.InputTextField
import com.linari.presentation.common.composables.MyLazyColumn
import com.linari.presentation.common.composables.MyScaffold
import com.linari.presentation.common.composables.MyText
import com.linari.presentation.common.composables.TopBar
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.SIDE_EFFECT_KEY
import com.linari.presentation.common.utils.ScreenTransition
import com.linari.presentation.counting.ConfirmDialog
import com.linari.presentation.pallet.contracts.PalletConfirmContract
import com.linari.presentation.shipping.contracts.PalletCustomerGroup
import com.linari.presentation.shipping.contracts.ShippingDetailContract
import com.linari.presentation.shipping.viewmodels.ShippingDetailViewModel
import com.linari.ui.theme.Background
import com.linari.ui.theme.ErrorRed
import com.linari.ui.theme.Gray1
import com.linari.ui.theme.Gray3
import com.linari.ui.theme.Gray4
import com.linari.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.exp


@Destination(style = ScreenTransition::class)
@Composable
fun ShippingDetailScreen(
    navigator: DestinationsNavigator,
    shipping: ShippingRow,
    viewModel: ShippingDetailViewModel = koinViewModel(
        parameters = {
            parametersOf(shipping)
        }
    )
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                ShippingDetailContract.Effect.NavBack -> {
                    navigator.popBackStack()
                }
            }
        }
    }
    ShippingDetailContent(state,onEvent)
}

@Composable
fun ShippingDetailContent(
    state: ShippingDetailContract.State = ShippingDetailContract.State(),
    onEvent: (ShippingDetailContract.Event)-> Unit = {}
) {
    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(Unit) {
        if (state.shipping?.shippingStatus != 2)focusRequester.requestFocus()
        onEvent(ShippingDetailContract.Event.FetchPalletData)
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(ShippingDetailContract.Event.CloseError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(ShippingDetailContract.Event.CloseToast)
        },
        onRefresh = {
            onEvent(ShippingDetailContract.Event.OnRefresh)
        }
    ) {
        Column(Modifier
            .fillMaxSize()
            .padding(15.mdp)) {
            TopBar(
                title = state.shipping?.shippingNumber?:"",
                subTitle = "Shipping",
                titleTag = state.warehouse?.name ?: "",
                onBack = {
                    onEvent(ShippingDetailContract.Event.OnNavBack)
                }
            )
            Spacer(modifier = Modifier.size(10.mdp))
            MyLazyColumn(
                items = state.palletList.groupBy { it.customerName }.mapNotNull { it.key?.let { key->
                    PalletCustomerGroup(key,it.value)
                } },
                itemContent = {_,it->
                    CustomerGroupView(
                        it.customer,
                        it.items,
                        removable = state.shipping?.shippingStatus != 2,
                        onClick = {
                            onEvent(ShippingDetailContract.Event.OnSelectPallet(it))
                        }
                    ) {
                        onEvent(ShippingDetailContract.Event.OnSelectForDelete(it))
                    }
                },
                header = {
                    Column(Modifier.background(Background)) {
                        if (state.shipping!=null){
                            ShippingItem(state.shipping)
                        }
                        if (state.shipping?.shippingStatus != 2){
                            Spacer(Modifier.size(10.mdp))
                            InputTextField(
                                state.barcode,
                                onValueChange = {
                                    onEvent(ShippingDetailContract.Event.OnChangeBarcode(it))
                                },
                                label = "Pallet Barcode",
                                onAny = {
                                    onEvent(ShippingDetailContract.Event.OnScan)
                                },
                                hideKeyboard = state.lockKeyboard,
                                trailingIcon = R.drawable.barcode,
                                loading = state.isScanning,
                                onTrailingClick = {
                                    onEvent(ShippingDetailContract.Event.OnScan)
                                },
                                focusRequester = focusRequester
                            )
                        }
                        Spacer(Modifier.size(10.mdp))
                    }
                },
                onReachEnd = {}
            )
        }
    }
    if (state.selectedForDelete!=null){
        ConfirmDialog(
            onDismiss = {
                onEvent(ShippingDetailContract.Event.OnSelectForDelete(null))
            },
            message = "Are you sure to remove [${state.selectedForDelete.palletBarcode}]?",
            isLoading = state.isDeleting
        ) {
            onEvent(ShippingDetailContract.Event.OnDelete(state.selectedForDelete))
        }
    }
    PalletProductSheet(state,onEvent)
}

@Composable
private fun CustomerGroupView(
    customer: String,
    items: List<PalletManifest>,
    removable: Boolean = true,
    onClick: (PalletManifest) -> Unit,
    onRemove: (PalletManifest) -> Unit
) {
    var expended by remember {
        mutableStateOf(true)
    }
    Column(
        Modifier
            .shadow(1.mdp, RoundedCornerShape(8.mdp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.mdp))
            .clickable {
                expended = !expended
            }
            .background(Color.White)
            .animateContentSize()
            .padding(10.mdp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            MyText(
                "${customer.trim().trimIndent()}(${items.first().customerCode})",
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            MyText(
                "Pallet Qty = ${items.size}",
                fontSize = 15.sp
            )
        }
        AnimatedVisibility(expended) {
            Column {
                Spacer(Modifier.size(10.mdp))

                items.forEach {
                    PalletBarcode(
                        it,
                        onClick = {onClick(it)},
                        removable = removable,
                        onRemove = {onRemove(it)}
                    )
                    Spacer(Modifier.size(5.mdp))
                }
            }
        }
    }
}

@Composable
fun PalletBarcode(
    model: PalletManifest,
    onClick: ()-> Unit,
    removable: Boolean = true,
    onRemove: ()->Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        positionalThreshold = {it*0.25f},
        confirmValueChange = {
            when(it){
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.EndToStart -> {
                    onRemove()
                    false
                }
                SwipeToDismissBoxValue.Settled -> {
                    true
                }
            }
        }
    )
    if (removable){
        SwipeToDismissBox(
            state = swipeState,
            enableDismissFromEndToStart = false,
            backgroundContent = {
                Row(
                    Modifier
                        .shadow(1.mdp, RoundedCornerShape(6.mdp))
                        .fillMaxSize()
                        .clip(RoundedCornerShape(6.mdp))
                        .background(ErrorRed)
                        .padding(horizontal = 10.mdp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if(swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart) Spacer(Modifier.size(5.mdp))
                    AnimatedVisibility(swipeState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "",
                                modifier = Modifier.size(20.mdp),
                                tint = Color.White
                            )
                            Spacer(Modifier.size(10.mdp))
                            MyText(
                                "Remove",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                    AnimatedVisibility(swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MyText(
                                "Remove",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 13.sp,
                                color = Color.White
                            )
                            Spacer(Modifier.size(10.mdp))
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "",
                                modifier = Modifier.size(20.mdp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        ) {
            Row(
                Modifier
                    .shadow(1.mdp, RoundedCornerShape(6.mdp))
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.mdp))
                    .clickable {
                        onClick()
                    }
                    .background(Gray1)
                    .padding(vertical = 6.mdp, horizontal = 8.mdp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    MyText(
                        text = "#${model.palletBarcode?:""}",
                        fontSize = 13.sp,
                        lineHeight = 13.sp,
                        fontWeight = FontWeight.W500,
                    )

                }
                if (model.total!=null){
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.mdp))
                            .background(Primary.copy(0.2f))
                            .padding(vertical = 4.mdp, horizontal = 10.mdp)
                    ) {
                        MyText(
                            text = model.total.removeZeroDecimal(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.W500,
                            color = Primary
                        )
                    }
                }
            }
        }
    } else {
        Row(
            Modifier
                .shadow(1.mdp, RoundedCornerShape(6.mdp))
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.mdp))
                .clickable {
                    onClick()
                }
                .background(Gray1)
                .padding(vertical = 6.mdp, horizontal = 8.mdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                MyText(
                    text = "#${model.palletBarcode?:""}",
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.W500,
                )

            }
            if (model.total!=null){
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.mdp))
                        .background(Primary.copy(0.2f))
                        .padding(vertical = 4.mdp, horizontal = 10.mdp)
                ) {
                    MyText(
                        text = model.total.removeZeroDecimal(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.W500,
                        color = Primary
                    )
                }
            }
        }
    }

}
@Composable
private fun ShippingItem(
    model: ShippingRow
) {

    var expend by remember {
        mutableStateOf(false)
    }

    Column(
        Modifier
            .shadow(1.mdp, RoundedCornerShape(6.mdp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.mdp))
            .clickable {
                expend = !expend
            }
            .background(Color.White)
            .animateContentSize()


    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(15.mdp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
//
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.mdp))
                        .background(Primary.copy(0.2f))
                        .padding(vertical = 4.mdp, horizontal = 10.mdp)
                ) {
                    MyText(
                        text = when(model.shippingStatus){
                            0->"confirm"
                            1->"wait for invoice"
                            2->"wait for confirm pallet"
                            else -> ""
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                }
                MyText(
                    text = "#${model.shippingNumber?:""}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )

            }
            Spacer(Modifier.size(10.mdp))
            DetailCard(
                "Customer",
                icon = R.drawable.profile_2user,
                detail = model.customerName?.replace(",","\n")?:"",
                showFullDetail = true
            )
            Spacer(Modifier.size(10.mdp))
            AnimatedVisibility(expend){
                Column(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth()) {
                        DetailCard(
                            "Driver",
                            icon = R.drawable.user_square,
                            detail = model.driverFullName?:"",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.size(5.mdp))
                        DetailCard(
                            "Driver ID",
                            icon = R.drawable.vuesax_linear_user_tag,
                            detail = model.driverTin?:"",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.size(10.mdp))
                    Row(Modifier.fillMaxWidth()) {
                        DetailCard(
                            "Car No.",
                            icon = R.drawable.truck_next,
                            detail = model.carNumber?:"",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.size(5.mdp))
                        DetailCard(
                            "Trailer No.",
                            icon = R.drawable.vuesax_outline_truck_tick,
                            detail = model.trailerNumber?:"",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.size(10.mdp))
                    Row(Modifier.fillMaxWidth()) {
                        DetailCard(
                            "Pallet Count",
                            icon = R.drawable.box,
                            detail = model.palletCount?.toString()?:"",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.size(5.mdp))
                        DetailCard(
                            "Product Count",
                            icon = R.drawable.keyboard2,
                            detail = model.sumPalletQty?.removeZeroDecimal()?:"",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.size(10.mdp))
                    DetailCard(
                        "Date",
                        icon = R.drawable.vuesax_linear_calendar_2,
                        detail = (model.date?:"") +(if (model.date!=null && model.time!=null) "," else "") + (model.time?:"")
                    )
                    Spacer(modifier = Modifier.size(15.mdp))
                }
            }
            Spacer(Modifier.size(10.mdp))
        }


    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PalletProductSheet(
    state: ShippingDetailContract.State,
    onEvent: (ShippingDetailContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (state.selectedPallet!=null){
        LaunchedEffect(Unit) {
            onEvent(ShippingDetailContract.Event.FetchPalletProducts(state.selectedPallet))
        }
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(ShippingDetailContract.Event.OnSelectPallet(null))
            },
            containerColor = Color.White
        ) {
            Box(Modifier.fillMaxWidth().heightIn(min = 400.mdp)) {

                Column (
                    Modifier
                        .padding(horizontal = 24.mdp)
                        .padding(bottom = 24.mdp)
                ) {
                    MyText(
                        text = "Pallet Details",
                        fontWeight = FontWeight.W500,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.size(12.mdp))
                    MyLazyColumn(
                        items = state.productList,
                        itemContent = { _, it ->
                            PalletProduct(it)
                        },
                        onReachEnd = {
                        }
                    )
                    Spacer(Modifier.size(15.mdp))
                }
                if (state.isProductLoading) CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}



@Composable
fun PalletProduct(
    model: ShippingDetailListOfPalletRow
) {
    var expended by remember {
        mutableStateOf(false)
    }
    BaseListItem(
        onClick = {
            expended = !expended
        },
        scan = null,
        quantity = (model.quantity?.removeZeroDecimal()?:"")+if(model.isWeight == true) " kg" else "",
        quantityTitle = "Quantity",
        item1 = BaseListItemModel("Product Name",model.productName?:"",R.drawable.vuesax_outline_3d_cube_scan),
        item2 = if (expended)BaseListItemModel("Product Code",model.productCode?:"",R.drawable.keyboard2) else null,
        item3 = if (expended)BaseListItemModel("Barcode",model.productBarcodeNumber?:"",R.drawable.barcode) else null,
        item4 = if (expended)BaseListItemModel(if (model.referenceNumberPO == model.referenceNumberLPO) "Reference No." else "Reference No. PO",model.referenceNumberPO?:"",R.drawable.hashtag) else null,
        item5 = if (expended && model.referenceNumberPO != model.referenceNumberLPO) BaseListItemModel("Reference No. LPO", model.referenceNumberLPO?:"",R.drawable.hashtag) else null,
        item6 = if (expended)BaseListItemModel("Exp Date",model.expireDate?:"",R.drawable.vuesax_linear_calendar_2) else null,
        item7 = if (expended)BaseListItemModel("Batch No.",model.batchNumber?:"",R.drawable.keyboard) else null,
    )
}