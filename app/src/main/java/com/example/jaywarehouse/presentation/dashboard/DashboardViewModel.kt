package com.example.jaywarehouse.presentation.dashboard

import com.example.jaywarehouse.presentation.common.utils.BaseViewModel

class DashboardViewModel : BaseViewModel<DashboardContract.Event,DashboardContract.State,DashboardContract.Effect>(){
    override fun setInitState(): DashboardContract.State {
        return DashboardContract.State()
    }

    override fun onEvent(event: DashboardContract.Event) {
        when(event){
            is DashboardContract.Event.OnNavigate -> setEffect {
                DashboardContract.Effect.Navigate(event.destination)
            }
        }
    }
}