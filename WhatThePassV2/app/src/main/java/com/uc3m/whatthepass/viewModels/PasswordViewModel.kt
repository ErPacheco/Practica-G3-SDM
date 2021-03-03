package com.uc3m.whatthepass.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.uc3m.whatthepass.models.*
import com.uc3m.whatthepass.util.Hash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PasswordViewModel(application: Application): AndroidViewModel(application) {

    private val repository: PasswordRepository
    val readAll: LiveData<List<Password>>
    init {
        val passwordDao = WhatTheDatabase.getDatabase(application).passwordDao()
        repository = PasswordRepository(passwordDao)
        readAll = repository.readAll
    }

    fun addPassword(name: String, emailUser: String, password: String, url: String){
        viewModelScope.launch(Dispatchers.IO) {
            val masterPass = Hash.sha512Hash(password)
            repository.addPassword(name, emailUser, masterPass, url)
        }
    }
}