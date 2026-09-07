package com.fahim.learncoroutinesbytutorials.utils

import com.fahim.learncoroutinesbytutorials.data.local.DatabaseHelper
import com.fahim.learncoroutinesbytutorials.data.local.entity.User

/**
 * In-memory [DatabaseHelper].
 */
class FakeDatabaseHelper(
    initialUsers: List<User> = emptyList(),
    var error: Throwable? = null
) : DatabaseHelper {

    var stored: List<User> = initialUsers
        private set

    override suspend fun getUsers(): List<User> {
        error?.let { throw it }
        return stored
    }

    override suspend fun insertAll(users: List<User>) {
        error?.let { throw it }
        stored = stored + users
    }
}
