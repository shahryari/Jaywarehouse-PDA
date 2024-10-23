package com.example.jaywarehouse.presentation.common.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface UiEvent

interface UiState

interface UiSideEffect

const val SIDE_EFFECT_KEY = "side_effect_key"

abstract class BaseViewModel
    <Event: UiEvent,
        State: UiState,
        Effect: UiSideEffect>
    : ViewModel() {
    private val initialState: State by lazy { setInitState() }

    init {
        subscribeToEvents()
    }
    abstract fun setInitState() : State

    abstract fun onEvent(event: Event)


    private val _state : MutableState<State> = mutableStateOf(initialState)
    val state by _state

    private val _effect : Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    private val _event = MutableSharedFlow<Event>()

    private fun subscribeToEvents() {
        viewModelScope.launch(Dispatchers.Main) {
            _event.collect {
                onEvent(it)
            }
        }
    }

    fun setEvent(event: Event) {
        viewModelScope.launch(Dispatchers.Main) {
            _event.emit(event)
        }
    }

    protected fun setState(reducer: State.()->State) {
        val newState = state.reducer()
        _state.value = newState
    }

    protected suspend fun setSuspendedState(reducer: State.() -> State) {
        withContext(Dispatchers.Main) {
            val newState = state.reducer()
            _state.value = newState
        }
    }
    protected fun setEffect(builder: ()->Effect) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }




}