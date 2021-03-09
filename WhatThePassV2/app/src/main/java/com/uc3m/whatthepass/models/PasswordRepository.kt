package com.uc3m.whatthepass.models

import androidx.lifecycle.LiveData

class PasswordRepository(private val passwordDao: PasswordDao) {

    val readAll: LiveData<List<Password>> = passwordDao.readAll()

    suspend fun addPassword(name: String, emailUser: String, email: String, user: String, password: String, url: String) {
        val pass = Password(0, name, emailUser, email, user, password, url)
        passwordDao.addPassword(pass)
    }

    suspend fun findPasswordByUser (user: String){
        passwordDao.findByUser(user)
    }

    suspend fun deletePasswordByUser (user:String){
        passwordDao.deleteByUser(user)
    }

    suspend fun deletePassword (pass:Password){
        passwordDao.deletePassword(pass)
    }
}