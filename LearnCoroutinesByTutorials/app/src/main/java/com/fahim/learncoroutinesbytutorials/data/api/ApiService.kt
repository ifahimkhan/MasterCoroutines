package com.fahim.learncoroutinesbytutorials.data.api

import com.fahim.learncoroutinesbytutorials.data.model.ApiUser
import retrofit2.http.GET

/**
 * Retrofit service. Every function is `suspend`, so Retrofit runs the request
 * on its own background thread and resumes the calling coroutine with the result.
 */
interface ApiService {

    @GET("users")
    suspend fun getUsers(): List<ApiUser>

    @GET("more-users")
    suspend fun getMoreUsers(): List<ApiUser>

    @GET("error")
    suspend fun getUsersWithError(): List<ApiUser>
}
