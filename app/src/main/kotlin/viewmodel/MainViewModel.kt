package jp.senchan.android.wasatter.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.senchan.android.wasatter.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _currentScreen: MutableStateFlow<Screen> by lazy {
        val currentScreen = savedStateHandle.get<Screen>("currentScreen")
        MutableStateFlow(currentScreen ?: Screen.OAuthLogin)
    }
    val currentScreen: StateFlow<Screen> get() = _currentScreen

    init {
        _currentScreen
            .onEach { savedStateHandle.set("currentScreen", it) }
            .launchIn(viewModelScope)
    }
}