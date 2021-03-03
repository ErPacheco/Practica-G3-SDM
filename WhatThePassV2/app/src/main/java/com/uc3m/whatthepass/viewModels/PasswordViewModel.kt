package com.uc3m.whatthepass.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.uc3m.whatthepass.models.*
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

        fun addPassword(password: Password){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addPassword(password)
        }
    }
    fun deletePasswordByUser(user: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePasswordByUser(user)
        }
    }
}