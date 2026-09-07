package com.fahim.learncoroutinesbytutorials.ui.base

/**
 * Single source of truth for what a screen should render.
 * ViewModels expose `StateFlow<UiState<T>>` and Compose collects it.
 */
sealed interface UiState<out T> {

    data class Success<T>(val data: T) : UiState<T>

    data class Error(val message: String) : UiState<Nothing>

    data object Loading : UiState<Nothing>
}
