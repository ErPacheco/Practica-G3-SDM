package com.uc3m.whatthepass.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class User(
    @PrimaryKey
    val email: String, // Email del usuario
    val masterPass: String, // Contraseña maestra del usuario

)
