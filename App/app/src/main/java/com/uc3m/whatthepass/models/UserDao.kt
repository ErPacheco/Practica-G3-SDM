package com.uc3m.whatthepass.models

import android.app.admin.DevicePolicyManager
import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addUser(user: User)

    @Query("SELECT * from User where email = :email and masterPass = :pass")
    suspend fun findUserByEmailAndMasterPass(email: String, pass: String): User?

    @Query("SELECT * from User where email = :email")
    suspend fun findUserByEmail(email: String): User

    // Preguntar al profesor, sobre como identificar al usuario que va a ser actualizado
    // si modificamos su clave primaria, en este caso el email
    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateUser(vararg user: User)

    @Delete
    suspend fun deleteUser(vararg user: User)


}