package com.fahim.learncoroutinesbytutorials.data.api

import com.fahim.learncoroutinesbytutorials.data.model.ApiUser

interface ApiHelper {

    suspend fun getUsers(): List<ApiUser>

    suspend fun getMoreUsers(): List<ApiUser>

    suspend fun getUsersWithError(): List<ApiUser>
}
