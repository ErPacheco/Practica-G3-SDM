package com.uc3m.whatthepass.models

import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PasswordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPassword(password: Password)

    // Busca las contraseñas de un usuario
    @Query("SELECT * from Password where user = :email")
    fun findByUser(email: String): LiveData<List<Password>>

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updatePassword(password: Password)

    @Delete
    suspend fun deletePassword(password: Password)

    @Query("SELECT * FROM Password ORDER BY id ASC")
    fun readAll(): LiveData<List<Password>>

    @Query("DELETE FROM Password where user=:email")
    suspend fun deleteByUser(email:String)
}