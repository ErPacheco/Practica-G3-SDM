package com.uc3m.whatthepass.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uc3m.whatthepass.models.Password
import com.uc3m.whatthepass.models.PasswordRepository
import com.uc3m.whatthepass.models.WhatTheDatabase
import com.uc3m.whatthepass.util.Hash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PasswordViewModel(application: Application) : AndroidViewModel(application) {
    // Contraseña compartida entre los diferentes fragmentos
    val message = MutableLiveData<Password>()
    private val repository: PasswordRepository
    // Lista de contraseñas de un usuario
    var readUserPasswords: LiveData<List<Password>>

    init {
        val passwordDao = WhatTheDatabase.getDatabase(application).passwordDao()
        repository = PasswordRepository(passwordDao)
        readUserPasswords = MutableLiveData(emptyList()) // Inicialización vacía de la lista
    }

    // Función que crea la lista de contraseñas de un usuario
    fun findPasswordsByUser(email: String) {
        readUserPasswords = repository.findPasswordByUser(email)
    }

    // Función para añadir una contraseña a la base de datos
    fun addPassword(name: String, emailUser: String, email: String, user: String, password: String, url: String, masterPass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val passwordToAdd = Hash.encrypt(password, masterPass)
            repository.addPassword(name, emailUser, email, user, passwordToAdd, url)
        }
    }

    // Función para eliminar una contraseña de la base de datos
    fun deletePassword(password: Password) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePassword(password)
        }
    }

    // Función para añadir al valor message una contraseña
    fun sentPassword(msg: Password) {
        message.value = msg
    }

    // Función para editar una contraseña
    fun updatePassword(id: Long, name: String, emailUser: String, email: String, user: String, password: String, url: String, masterPass: String) {
        viewModelScope.launch {
            val passwordToChange = Hash.encrypt(password, masterPass)
            repository.updatePassword(id, name, emailUser, email, user, passwordToChange, url)
        }
    }
    // Función para borrar todas las contraseñas de un usuario
    fun deletePasswordByUser(user: String) {
        viewModelScope.launch {
            repository.deletePasswordByUser(user)
        }
    }
}
