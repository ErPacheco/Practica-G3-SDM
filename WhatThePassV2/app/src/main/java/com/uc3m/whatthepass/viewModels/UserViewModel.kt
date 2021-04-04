package com.uc3m.whatthepass.viewModels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.uc3m.whatthepass.models.User
import com.uc3m.whatthepass.models.UserRepository
import com.uc3m.whatthepass.models.WhatTheDatabase
import com.uc3m.whatthepass.util.Hash
import com.uc3m.whatthepass.util.Hash.bcryptHash
import com.uc3m.whatthepass.util.Hash.verifyHash

class UserViewModel(application: Application): AndroidViewModel(application) {

    private val readAll: LiveData<List<User>>
    private val repository: UserRepository
    private var logged: Boolean = false
    private var register: Boolean = false

    init {
        val userDao = WhatTheDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        readAll = repository.readAll
    }

    suspend fun addUser(email: String, password: String): Boolean {
        val userFind = repository.readUserEmail(email)
        if(userFind == null) {
            val hashPassword = bcryptHash(password)
            repository.addUser(email, hashPassword)
            register = true
        } else {
            register = true
        }
        return register
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        val userFind = repository.readUserEmail(email)
        val res: Boolean
        if(userFind != null) {
            res = verifyHash(password, userFind.masterPass)
        } else {
            res = false
        }

        return res
    }

    suspend fun findUserByEmail(email: String): User? {
        return repository.readUserEmail(email)
    }
}