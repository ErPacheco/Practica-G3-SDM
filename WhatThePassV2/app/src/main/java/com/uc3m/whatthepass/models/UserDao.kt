package com.uc3m.whatthepass.models

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {

    @Query("SELECT * FROM User ORDER BY email ASC")
    fun readAll(): LiveData<List<User>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addUser(user: User)

    @Query("SELECT * from User where email = :email and masterPass = :pass")
    suspend fun findUserByEmailAndMasterPass(email: String, pass: String): User?

    @Query("SELECT * from User where email = :email")
    suspend fun findUserByEmail(email: String): User?

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateUser(vararg user: User)

    @Delete
    suspend fun deleteUser(vararg user: User)
}
