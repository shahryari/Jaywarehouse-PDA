package com.example.jaywarehouse.data.common.modules

import com.example.jaywarehouse.data.auth.authModule
import com.example.jaywarehouse.data.packing.model.PackingDetailModel
import com.example.jaywarehouse.data.packing.packingModule
import com.example.jaywarehouse.data.picking.pickingModule
import com.example.jaywarehouse.data.putaway.putawayModule
import com.example.jaywarehouse.data.receiving.receivingModule
import com.example.jaywarehouse.data.shipping.shippingModule
import com.example.jaywarehouse.data.transfer.transferModule
import com.example.jaywarehouse.presentation.auth.LoginViewModel
import com.example.jaywarehouse.presentation.counting.viewmodels.CountingDetailViewModel
import com.example.jaywarehouse.presentation.counting.viewmodels.CountingViewModel
import com.example.jaywarehouse.presentation.dashboard.DashboardViewModel
import com.example.jaywarehouse.presentation.main.MainViewModel
import com.example.jaywarehouse.presentation.packing.viewmodels.PackingDetailViewModel
import com.example.jaywarehouse.presentation.packing.viewmodels.PackingViewModel
import com.example.jaywarehouse.presentation.picking.viewmodels.PickingCustomerViewModel
import com.example.jaywarehouse.presentation.picking.viewmodels.PickingDetailViewModel
import com.example.jaywarehouse.presentation.picking.viewmodels.PickingListViewModel
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
    includes(receivingModule, putawayModule, authModule, prefsModule, pickingModule, packingModule, shippingModule,
        transferModule)

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
        PutawayDetailViewModel(get(),get(),it.get(),it.get())
    }

    viewModel {
        CountingDetailViewModel(get(),get(),it.get())
    }

    viewModel {
        PickingCustomerViewModel(get(),get())
    }

    viewModel {
        PickingListViewModel(get(),get(),it.get())
    }
    viewModel {
        PickingDetailViewModel(get(),get(),it.get(),it.get(),it.get())
    }
    viewModel {
        PackingViewModel(get(),get())
    }
    viewModel {
        PackingDetailViewModel(get(),get(),it.get())
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