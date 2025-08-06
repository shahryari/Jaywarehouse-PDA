package com.linari.data.common.modules

import com.linari.data.auth.authModule
import com.linari.data.checking.checkingModule
import com.linari.data.cycle_count.cycleModule
import com.linari.data.loading.loadingModule
import com.linari.data.manual_putaway.manualPutawayModule
import com.linari.data.packing.packingModule
import com.linari.data.pallet.palletModule
import com.linari.data.picking.models.PurchaseOrderDetailListBDRow
import com.linari.data.picking.models.PurchaseOrderListBDRow
import com.linari.data.picking.pickingModule
import com.linari.data.putaway.putawayModule
import com.linari.data.receiving.model.ReceivingDetailRow
import com.linari.data.receiving.model.ReceivingRow
import com.linari.data.receiving.receivingModule
import com.linari.data.rs.rSModule
import com.linari.data.shipping.shippingModule
import com.linari.data.transfer.transferModule
import com.linari.presentation.auth.LoginViewModel
import com.linari.presentation.checking.viewModels.CheckingDetailViewModel
import com.linari.presentation.checking.viewModels.CheckingViewModel
import com.linari.presentation.counting.viewmodels.CountingDetailViewModel
import com.linari.presentation.counting.viewmodels.CountingInceptionViewModel
import com.linari.presentation.counting.viewmodels.CountingViewModel
import com.linari.presentation.cycle_count.viewmodels.CycleDetailViewModel
import com.linari.presentation.cycle_count.viewmodels.CycleViewModel
import com.linari.presentation.dashboard.DashboardViewModel
import com.linari.presentation.loading.viewmodels.LoadingDetailViewModel
import com.linari.presentation.loading.viewmodels.LoadingViewModel
import com.linari.presentation.manual_putaway.viewmodels.ManualPutawayDetailViewModel
import com.linari.presentation.manual_putaway.viewmodels.ManualPutawayViewModel
import com.linari.presentation.pallet.viewmodels.PalletConfirmViewModel
import com.linari.presentation.picking.viewModels.PickingDetailViewModel
import com.linari.presentation.picking.viewModels.PickingViewModel
import com.linari.presentation.putaway.viewmodels.PutawayDetailViewModel
import com.linari.presentation.manual_putaway.viewmodels.PutawayViewModel
import com.linari.presentation.pallet.viewmodels.PalletProductViewModel
import com.linari.presentation.picking.viewModels.PickingListBDViewModel
import com.linari.presentation.picking.viewModels.PurchaseOrderDetailViewModel
import com.linari.presentation.picking.viewModels.PurchaseOrderViewModel
import com.linari.presentation.rs.viewmodels.RSIntegrationViewModel
import com.linari.presentation.rs.viewmodels.WaybillViewModel
import com.linari.presentation.shipping.viewmodels.ShippingDetailViewModel
import com.linari.presentation.shipping.viewmodels.ShippingViewModel
import com.linari.presentation.transfer.viewmodels.TransferViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    includes(
        receivingModule,
        putawayModule,
        authModule,
        prefsModule,
        pickingModule,
        packingModule,
        shippingModule,
        transferModule,
        manualPutawayModule,
        checkingModule,
        palletModule,
        loadingModule,
        cycleModule,
        rSModule
    )

    viewModel {
        LoginViewModel(get(),get())
    }

    viewModel {
        DashboardViewModel(get(),get())
    }

    viewModel {
        CountingViewModel(get(),it.get(),get())
    }

    viewModel {
        PutawayViewModel(get(),get())
    }

    viewModel {
        PutawayDetailViewModel(get(),get(),it.get())
    }

    viewModel {
        CountingDetailViewModel(get(),get(),it.get<Boolean>(),it.get<ReceivingRow>())
    }

    viewModel {
        CountingInceptionViewModel(get(),get(), it.get<ReceivingDetailRow>(), it.get<Boolean>(),it.get<Int>(),)
    }

    viewModel {
        ManualPutawayViewModel(get(),it.get(),get())
    }

    viewModel {
        ManualPutawayDetailViewModel(get(),get(),it.get())
    }

    viewModel {
        PickingViewModel(get(),get())
    }

    viewModel {
        PickingDetailViewModel(get(),get(),it.get())
    }

    viewModel {
        ShippingViewModel(get(),get())
    }

    viewModel {
        CheckingViewModel(get(),get())
    }

    viewModel {
        CheckingDetailViewModel(get(),get(),it.get())
    }

    viewModel {
        PalletConfirmViewModel(get(),get())
    }

    viewModel {
        LoadingViewModel(get(),get())
    }

    viewModel {
        LoadingDetailViewModel(get(),get(),it.get())
    }

    viewModel {
        TransferViewModel(get(),get())
    }

    viewModel {
        CycleViewModel(get(),get())
    }

    viewModel {
        CycleDetailViewModel(get(),get(),get(),it.get())
    }

    viewModel {
        RSIntegrationViewModel(get(),get())
    }

    viewModel {
        PurchaseOrderViewModel(get(),get())
    }

    viewModel {
        PurchaseOrderDetailViewModel(get(),get(),it.get())
    }

    viewModel {
        PickingListBDViewModel(get(),get(),it.get<PurchaseOrderListBDRow>(),it.get<PurchaseOrderDetailListBDRow>())
    }

    viewModel {
        ShippingDetailViewModel(get(),it.get(),get())
    }

    viewModel {
        PalletProductViewModel(get(),get(),get())
    }

    viewModel {
        WaybillViewModel(get(),get())
    }
}