package com.uc3m.whatthepass.viewModels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.uc3m.whatthepass.models.User
import com.uc3m.whatthepass.models.UserRepository
import com.uc3m.whatthepass.models.WhatTheDatabase
import com.uc3m.whatthepass.util.Hash
import com.uc3m.whatthepass.util.Hash.verifyHash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(application: Application): AndroidViewModel(application) {

    val readAll: LiveData<List<User>>
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
        if (userFind != null) {
            register = false
        } else {
            val masterPass = Hash.bcryptHash(password)
            repository.addUser(email, masterPass)
            register = true
        }
        return register
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        val userFind = repository.readUserEmail(email)
        if(userFind != null) {
            val res = verifyHash(password, userFind.masterPass)
            logged = res
        } else {
            logged = false
        }

        return logged
    }
}