package com.example.jaywarehouse.data.common.modules

import com.example.jaywarehouse.data.auth.authModule
import com.example.jaywarehouse.data.manual_putaway.manualPutawayModule
import com.example.jaywarehouse.data.packing.packingModule
import com.example.jaywarehouse.data.picking.pickingModule
import com.example.jaywarehouse.data.putaway.putawayModule
import com.example.jaywarehouse.data.receiving.receivingModule
import com.example.jaywarehouse.data.shipping.shippingModule
import com.example.jaywarehouse.data.transfer.transferModule
import com.example.jaywarehouse.presentation.auth.LoginViewModel
import com.example.jaywarehouse.presentation.counting.viewmodels.CountingDetailViewModel
import com.example.jaywarehouse.presentation.counting.viewmodels.CountingInceptionViewModel
import com.example.jaywarehouse.presentation.counting.viewmodels.CountingViewModel
import com.example.jaywarehouse.presentation.dashboard.DashboardViewModel
import com.example.jaywarehouse.presentation.main.MainViewModel
import com.example.jaywarehouse.presentation.manual_putaway.viewmodels.ManualPutawayDetailViewModel
import com.example.jaywarehouse.presentation.manual_putaway.viewmodels.ManualPutawayViewModel
import com.example.jaywarehouse.presentation.picking.viewModels.PickingDetailViewModel
import com.example.jaywarehouse.presentation.picking.viewModels.PickingViewModel
import com.example.jaywarehouse.presentation.profile.ProfileViewModel
import com.example.jaywarehouse.presentation.putaway.viewmodels.PutawayDetailViewModel
import com.example.jaywarehouse.presentation.putaway.viewmodels.PutawayViewModel
import com.example.jaywarehouse.presentation.shipping.viewmodels.ShippingDetailViewModel
import com.example.jaywarehouse.presentation.shipping.viewmodels.ShippingViewModel
import com.example.jaywarehouse.presentation.transfer.viewmodels.TransferPickViewModel
import com.example.jaywarehouse.presentation.transfer.viewmodels.TransferPutViewModel
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
        manualPutawayModule
    )

    viewModel {
        LoginViewModel(get(),get())
    }

    viewModel {
        MainViewModel(get(),get())
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
        ShippingDetailViewModel(get(),get(),it.get())
    }

    viewModel {
        ProfileViewModel(get(),get())
    }

    viewModel {
        TransferPickViewModel(get(),get())
    }

    viewModel {
        TransferPutViewModel(get(),get())
    }
}