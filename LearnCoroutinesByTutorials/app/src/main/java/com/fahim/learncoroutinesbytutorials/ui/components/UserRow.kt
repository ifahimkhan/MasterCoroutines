package com.fahim.learncoroutinesbytutorials.ui.components

import com.fahim.learncoroutinesbytutorials.data.local.entity.User
import com.fahim.learncoroutinesbytutorials.data.model.ApiUser

/**
 * Display model shared by API users and DB users so one list composable
 * can render both.
 */
data class UserRow(
    val id: Int,
    val name: String,
    val email: String,
    val avatar: String
)

fun ApiUser.toUserRow() = UserRow(id = id, name = name, email = email, avatar = avatar)

fun User.toUserRow() = UserRow(
    id = id,
    name = name.orEmpty(),
    email = email.orEmpty(),
    avatar = avatar.orEmpty()
)
