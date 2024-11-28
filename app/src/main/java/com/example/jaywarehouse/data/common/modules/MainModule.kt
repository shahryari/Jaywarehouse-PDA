package com.example.jaywarehouse.data.common.modules

import com.example.jaywarehouse.data.auth.authModule
import com.example.jaywarehouse.data.checking.checkingModule
import com.example.jaywarehouse.data.cycle_count.cycleModule
import com.example.jaywarehouse.data.loading.loadingModule
import com.example.jaywarehouse.data.manual_putaway.manualPutawayModule
import com.example.jaywarehouse.data.packing.packingModule
import com.example.jaywarehouse.data.pallet.palletModule
import com.example.jaywarehouse.data.picking.pickingModule
import com.example.jaywarehouse.data.putaway.putawayModule
import com.example.jaywarehouse.data.receiving.receivingModule
import com.example.jaywarehouse.data.rs.rSModule
import com.example.jaywarehouse.data.shipping.shippingModule
import com.example.jaywarehouse.data.transfer.transferModule
import com.example.jaywarehouse.presentation.auth.LoginViewModel
import com.example.jaywarehouse.presentation.checking.viewModels.CheckingDetailViewModel
import com.example.jaywarehouse.presentation.checking.viewModels.CheckingViewModel
import com.example.jaywarehouse.presentation.counting.viewmodels.CountingDetailViewModel
import com.example.jaywarehouse.presentation.counting.viewmodels.CountingInceptionViewModel
import com.example.jaywarehouse.presentation.counting.viewmodels.CountingViewModel
import com.example.jaywarehouse.presentation.cycle_count.viewmodels.CycleDetailViewModel
import com.example.jaywarehouse.presentation.cycle_count.viewmodels.CycleViewModel
import com.example.jaywarehouse.presentation.dashboard.DashboardViewModel
import com.example.jaywarehouse.presentation.loading.viewmodels.LoadingDetailViewModel
import com.example.jaywarehouse.presentation.loading.viewmodels.LoadingViewModel
import com.example.jaywarehouse.presentation.manual_putaway.viewmodels.ManualPutawayDetailViewModel
import com.example.jaywarehouse.presentation.manual_putaway.viewmodels.ManualPutawayViewModel
import com.example.jaywarehouse.presentation.pallet.PalletConfirmViewModel
import com.example.jaywarehouse.presentation.picking.viewModels.PickingDetailViewModel
import com.example.jaywarehouse.presentation.picking.viewModels.PickingViewModel
import com.example.jaywarehouse.presentation.putaway.viewmodels.PutawayDetailViewModel
import com.example.jaywarehouse.presentation.putaway.viewmodels.PutawayViewModel
import com.example.jaywarehouse.presentation.rs.RSIntegrationViewModel
import com.example.jaywarehouse.presentation.shipping.viewmodels.ShippingViewModel
import com.example.jaywarehouse.presentation.transfer.viewmodels.TransferViewModel
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
        CountingViewModel(get(),get())
    }

    viewModel {
        PutawayViewModel(get(),get())
    }

    viewModel {
        PutawayDetailViewModel(get(),get(),it.get())
    }

    viewModel {
        CountingDetailViewModel(get(),get(),it.get())
    }

    viewModel {
        CountingInceptionViewModel(get(),get(), it[0], it[1])
    }

    viewModel {
        ManualPutawayViewModel(get(),get())
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
        CycleDetailViewModel(get(),get(),it.get())
    }

    viewModel {
        RSIntegrationViewModel(get(),get())
    }
}