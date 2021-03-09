package com.uc3m.whatthepass.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Password::class, File::class], version = 3, exportSchema = false)
abstract class WhatTheDatabase:RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun passwordDao(): PasswordDao
    abstract fun FileDao(): FileDao

    companion object{

        @Volatile
        private var INSTANCE: WhatTheDatabase? = null

        fun getDatabase(context: Context): WhatTheDatabase{
            synchronized(this){
                var instance = INSTANCE
                if (instance == null){
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            WhatTheDatabase::class.java,
                            "what_the_database"
                    ).fallbackToDestructiveMigration().build()
                }
                return instance
            }
        }

    }
}