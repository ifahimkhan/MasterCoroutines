package com.fahim.learncoroutinesbytutorials.ui.basic

/**
 * One runnable experiment on the Basics screen.
 */
data class BasicExample(
    val title: String,
    val description: String,
    val run: () -> Unit
)
