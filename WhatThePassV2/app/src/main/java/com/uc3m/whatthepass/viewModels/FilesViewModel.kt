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

class FilesViewModel(application: Application): AndroidViewModel(application) {
    val message = MutableLiveData<File>()
    private val repository: FileRepository
    val readAll: LiveData<List<File>>
    init {
        val fileDao = WhatTheDatabase.getDatabase(application).FileDao()
        repository = FileRepository(fileDao)
        readAll = repository.readAll
    }

    fun addFile(name: String, path: String, user:String){
        viewModelScope.launch(Dispatchers.IO) {
            //val filedToAdd = Hash.encrypt(password, masterPass)
            repository.addFile(name, path, user)
        }
    }
    fun deleteFileByUser(user: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFileByUser(user)
        }
    }
    fun deleteFile(file: File){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFile(file)
        }
    }



    fun updatePassword(name: String, path: String, user:String)  {
        viewModelScope.launch {
            //val passwordToChange = Hash.encrypt(password, masterPass)
            repository.updateFile(name,path, user)
        }
    }
}