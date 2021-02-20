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
    suspend fun findByUser(email: String): LiveData<List<Password>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePassword(password: Password)

    @Delete
    suspend fun deletePassword(password: Password)
}