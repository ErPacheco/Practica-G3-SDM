package com.uc3m.whatthepass.util

import at.favre.lib.crypto.bcrypt.BCrypt
import org.bouncycastle.jcajce.provider.digest.Keccak
import java.nio.charset.Charset
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Hash {

    private val encoder = Base64.getEncoder()
    private val decoder = Base64.getDecoder()

    fun bcryptHash(str: String): String {
        return BCrypt.withDefaults().hashToString(12, str.toCharArray())
    }

    fun kekHashSubstring(str: String): String {
        val bArray: ByteArray = str.toByteArray(Charset.defaultCharset())
        val digest = Keccak.Digest512()
        digest.update(bArray, 0, bArray.size)
        val newDigest = digest.digest()
        val keccakHash: String = newDigest.joinToString("") { "%02x".format(it) }
        return keccakHash.substring(0, 10)
    }

    fun verifyHash(strInput: String, strSaved: String): Boolean {
        val res = BCrypt.verifyer().verify(strInput.toCharArray(), strSaved)
        return res.verified
    }

    private fun cipher(opmode: Int, secretKey: String): Cipher {
        var pad = ""
        if (secretKey.length < 32) {
            pad = secretKey.padEnd(32, '0')
        } else if (secretKey.length > 32) {
            pad = secretKey.substring(0, 32)
        }
        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val sk = SecretKeySpec(pad.toByteArray(Charsets.UTF_8), "AES")
        val iv = IvParameterSpec(pad.substring(0, 16).toByteArray(Charsets.UTF_8))
        c.init(opmode, sk, iv)
        return c
    }

    fun encrypt(str: String, secretKey: String): String {
        val encrypted = cipher(Cipher.ENCRYPT_MODE, secretKey).doFinal(str.toByteArray(Charsets.UTF_8))
        return String(encoder.encode(encrypted))
    }

    fun decrypt(str: String, secretKey: String?): String {
        return if (secretKey.isNullOrBlank()) {
            ""
        } else {
            val byteStr = decoder.decode(str.toByteArray(Charsets.UTF_8))
            String(cipher(Cipher.DECRYPT_MODE, secretKey).doFinal(byteStr))
        }
    }
}
