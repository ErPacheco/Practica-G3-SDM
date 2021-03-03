package com.uc3m.whatthepass.models

import androidx.lifecycle.LiveData

class PasswordRepository(private val passwordDao: PasswordDao) {
    val readAll: LiveData<List<Password>> = passwordDao.readAll()
    suspend fun addPassword(pass:Password) {
        passwordDao.addPassword(pass)
    }

    suspend fun findPasswordByUser (user: String){
        passwordDao.findByUser(user)
    }

    suspend fun deletePasswordByUser (user:String){
        passwordDao.deleteByUser(user)
    }
}