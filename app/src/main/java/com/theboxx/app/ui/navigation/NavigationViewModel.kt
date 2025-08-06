package com.theboxx.app.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NavigationViewModel() : ViewModel() {


    private val _navigationState = MutableStateFlow(NavigationState())

    val navigationState = _navigationState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000),
        NavigationState())


    fun onEvent(event: NavigationEvent) {
        viewModelScope.launch {
            when (event) {
                is NavigationEvent.SetCurrentScreen -> {
                    _navigationState.update {
                        it.copy(
                            currentScreen = event.screen
                        )
                    }
                }

                is NavigationEvent.SetAppSearchEnabled -> {
                    _navigationState.update {
                        it.copy(
                            isAppSearchEnabled = event.enabled
                        )
                    }
                }
            }
        }
    }




}
