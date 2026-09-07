package com.fahim.learncoroutinesbytutorials.util

import com.fahim.learncoroutinesbytutorials.ui.base.UiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class UiStateExtTest {

    @Test
    fun mapSuccess_transformsSuccessPayload() {
        val state: UiState<Int> = UiState.Success(2)

        val mapped = state.mapSuccess { it * 10 }

        assertEquals(UiState.Success(20), mapped)
    }

    @Test
    fun mapSuccess_leavesErrorUntouched() {
        val state: UiState<Int> = UiState.Error("nope")

        val mapped = state.mapSuccess { it * 10 }

        assertSame(state, mapped)
    }

    @Test
    fun mapSuccess_leavesLoadingUntouched() {
        val state: UiState<Int> = UiState.Loading

        val mapped = state.mapSuccess { it * 10 }

        assertSame(state, mapped)
    }
}
