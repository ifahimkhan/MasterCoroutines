package com.fahim.learncoroutinesbytutorials.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fahim.learncoroutinesbytutorials.data.local.dao.UserDao
import com.fahim.learncoroutinesbytutorials.data.local.entity.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}
