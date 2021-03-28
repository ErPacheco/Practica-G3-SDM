package com.uc3m.whatthepass.models

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "Password",
        //foreignKeys = [ForeignKey(entity = User::class, parentColumns = ["email"], childColumns = ["user"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)],
        indices = [Index(value = ["name"], unique = true)])
data class Password(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val name: String,// nombre de entrada
        @ForeignKey
                (entity = User::class,
                parentColumns = ["email"],
                childColumns = ["user"],
                onDelete = CASCADE,
                onUpdate = CASCADE
                )
        val user: String, // Usuario al que pertenece esta entrada
        val inputEmail: String?, // Email de la entrada de la contraseña
        val inputUser: String?, // Nombre de usuario de la entrada de la contraseña
       // @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val hashPassword: String,
        val url: String?
)


