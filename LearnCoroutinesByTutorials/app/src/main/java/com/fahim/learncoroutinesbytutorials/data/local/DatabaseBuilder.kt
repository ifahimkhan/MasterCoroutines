package com.fahim.learncoroutinesbytutorials.data.local

import android.content.Context
import androidx.room.Room

object DatabaseBuilder {

    private const val DATABASE_NAME = "learn-coroutines.db"

    @Volatile
    private var instance: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: buildRoomDB(context).also { instance = it }
        }
    }

    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
}
