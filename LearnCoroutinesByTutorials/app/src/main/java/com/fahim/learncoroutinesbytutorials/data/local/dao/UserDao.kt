package com.fahim.learncoroutinesbytutorials.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.fahim.learncoroutinesbytutorials.data.local.entity.User

/**
 * Room DAO with `suspend` functions. Room moves the query off the main thread
 * automatically and resumes the caller when the result is ready.
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    @Insert
    suspend fun insertAll(users: List<User>)

    @Delete
    suspend fun delete(user: User)
}
