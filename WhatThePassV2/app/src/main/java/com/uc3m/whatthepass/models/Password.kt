package com.uc3m.whatthepass.models

import androidx.room.* // ktlint-disable no-wildcard-imports
// ktlint-disable no-wildcard-imports
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "Password",
    indices = [Index(value = ["name"])]
)
data class Password(
    @PrimaryKey(autoGenerate = true)
    val id: Long, // identificador del password
    val name: String, // nombre de entrada
    @ForeignKey
    (
        entity = User::class,
        parentColumns = ["email"],
        childColumns = ["user"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )
    val user: String, // Usuario al que pertenece esta entrada
    val inputEmail: String?, // Email de la entrada de la contraseña
    val inputUser: String?, // Nombre de usuario de la entrada de la contraseña
    val hashPassword: String, // Contraseña de la entrada cifrada
    val url: String? // Url de la entrada
) {
    constructor() : this(
        0, "",
        "", "", "",
        "", ""
    )
}
