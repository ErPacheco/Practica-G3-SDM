package com.uc3m.whatthepass.util

import java.math.BigInteger
import java.security.MessageDigest

object Hash {

    fun sha512Hash(str: String): String {
        // Creamos la instancia de SHA-512
        val md = MessageDigest.getInstance("SHA-512")
        // Resumen del mensaje
        val messageDigest = md.digest(str.toByteArray())
        // Convierte el ByteArray en una representación numérica
        val no = BigInteger(1, messageDigest)
        // Convierte los numeros en string
        var hashtext = no.toString(16)

        /*while (hashtext.length < 32) {
            hashtext = "0$hashtext"
        }*/

        return hashtext

    }
}