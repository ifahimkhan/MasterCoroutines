package com.fahim.learncoroutinesbytutorials.util

import com.fahim.learncoroutinesbytutorials.ui.base.UiState

/**
 * Transform the payload of a Success state, leaving Loading / Error untouched.
 */
inline fun <T, R> UiState<T>.mapSuccess(transform: (T) -> R): UiState<R> = when (this) {
    is UiState.Success -> UiState.Success(transform(data))
    is UiState.Error -> this
    is UiState.Loading -> this
}
