package com.uc3m.whatthepass.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uc3m.whatthepass.models.*
import com.uc3m.whatthepass.util.Hash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PasswordViewModel(application: Application): AndroidViewModel(application) {
    val message = MutableLiveData<Password>()
    private val repository: PasswordRepository
    val readAll: LiveData<List<Password>>
    init {
        val passwordDao = WhatTheDatabase.getDatabase(application).passwordDao()
        repository = PasswordRepository(passwordDao)
        readAll = repository.readAll
    }

    fun addPassword(name: String, emailUser: String, email: String, user: String, password: String, url: String, masterPass: String){
        viewModelScope.launch(Dispatchers.IO) {
            val passwordToAdd = Hash.encrypt(password, masterPass)
            repository.addPassword(name, emailUser, email, user, passwordToAdd, url)
        }
    }
    fun deletePasswordByUser(user: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePasswordByUser(user)
        }
    }
    fun deletePassword(password:Password){
        viewModelScope.launch(Dispatchers.IO) {
           repository.deletePassword(password)
        }
    }

    fun sentPassword(msg:Password){
        message.value = msg
    }
}