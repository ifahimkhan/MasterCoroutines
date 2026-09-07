package com.fahim.learncoroutinesbytutorials.data.local

import com.fahim.learncoroutinesbytutorials.data.local.entity.User

interface DatabaseHelper {

    suspend fun getUsers(): List<User>

    suspend fun insertAll(users: List<User>)
}
