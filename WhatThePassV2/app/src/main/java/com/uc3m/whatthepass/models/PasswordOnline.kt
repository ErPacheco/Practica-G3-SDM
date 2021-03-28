package com.uc3m.whatthepass.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.HashMap


@IgnoreExtraProperties

data class PasswordOnline(
    val id: Int?,
    val name: String?,// nombre de entrada

    val user: String?, // Usuario al que pertenece esta entrada
    val inputEmail: String?, // Email de la entrada de la contraseña
    val inputUser: String?, // Nombre de usuario de la entrada de la contraseña

    val hashPassword: String?,
    val url: String?){
    fun toMap(): Map<String, Any?> {
        return mapOf(
                "id" to id,
                "name" to name,
                "user" to user,
                "inputEmail" to inputEmail,
                "inputUser" to inputUser,
                "hashPassword" to hashPassword,
                "url" to url
        )
    }
}
