package com.uc3m.whatthepass.models

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FileDao {
    // DUDA: nos devuelve si lo especificamos los datos del usuario insertado?
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFile(file: File)

    // Busca los ficheros de un usuario
    @Query("SELECT * from File where user = :email")
    suspend fun findByUser(email: String): List<File>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateFile(file: File)

    @Delete
    suspend fun deleteFile(file: File)

    @Query("SELECT * FROM File ORDER BY id ASC")
    fun readAll(): LiveData<List<File>>

    @Query("DELETE FROM Password where user=:email")
    suspend fun deleteByUser(email:String)
}