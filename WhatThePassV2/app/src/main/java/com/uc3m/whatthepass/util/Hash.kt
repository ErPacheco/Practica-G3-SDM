package com.uc3m.whatthepass.util

import at.favre.lib.crypto.bcrypt.BCrypt

object Hash {

    fun bcryptHash(str: String): String {
        return BCrypt.withDefaults().hashToString(12, str.toCharArray())
    }

    fun verifyHash(strInput: String, strSaved: String): Boolean {
        val res = BCrypt.verifyer().verify(strInput.toCharArray(), strSaved)
        return res.verified
    }
}