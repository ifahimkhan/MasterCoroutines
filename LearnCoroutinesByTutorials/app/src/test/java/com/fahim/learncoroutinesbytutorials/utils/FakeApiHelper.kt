package com.fahim.learncoroutinesbytutorials.utils

import com.fahim.learncoroutinesbytutorials.data.api.ApiHelper
import com.fahim.learncoroutinesbytutorials.data.model.ApiUser
import kotlinx.coroutines.delay

/**
 * Scriptable [ApiHelper]. Each endpoint returns its configured result or
 * throws its configured error, after an optional virtual-time delay.
 */
class FakeApiHelper(
    var users: List<ApiUser> = emptyList(),
    var moreUsers: List<ApiUser> = emptyList(),
    var usersError: Throwable? = null,
    var moreUsersError: Throwable? = null,
    var usersWithErrorError: Throwable? = RuntimeException("endpoint always fails"),
    var delayMs: Long = 0L
) : ApiHelper {

    var getUsersCalls: Int = 0
        private set
    var getMoreUsersCalls: Int = 0
        private set
    var getUsersWithErrorCalls: Int = 0
        private set

    override suspend fun getUsers(): List<ApiUser> {
        getUsersCalls++
        delay(delayMs)
        usersError?.let { throw it }
        return users
    }

    override suspend fun getMoreUsers(): List<ApiUser> {
        getMoreUsersCalls++
        delay(delayMs)
        moreUsersError?.let { throw it }
        return moreUsers
    }

    override suspend fun getUsersWithError(): List<ApiUser> {
        getUsersWithErrorCalls++
        delay(delayMs)
        usersWithErrorError?.let { throw it }
        return emptyList()
    }
}
