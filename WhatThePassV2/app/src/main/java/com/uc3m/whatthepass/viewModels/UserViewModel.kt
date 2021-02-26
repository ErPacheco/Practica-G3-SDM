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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(application: Application): AndroidViewModel(application) {

    val readAll: LiveData<List<User>>
    private val repository: UserRepository
    private var logged: Boolean = false

    init {
        val userDao = WhatTheDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        readAll = repository.readAll
    }

    fun addUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val masterPass = Hash.sha512Hash(password)
            repository.addUser(email, masterPass)
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        val masterPass = Hash.sha512Hash(password)
        val userFind = repository.readUser(email, masterPass)
        if (userFind != null) {
            logged = true
        } else {
            logged = false
        }
        return logged
    }
}