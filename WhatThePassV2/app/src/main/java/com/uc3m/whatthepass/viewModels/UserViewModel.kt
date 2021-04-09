package com.uc3m.whatthepass.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.uc3m.whatthepass.models.User
import com.uc3m.whatthepass.models.UserRepository
import com.uc3m.whatthepass.models.WhatTheDatabase
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

    // Función para añadir un usuario a la base de datos
    suspend fun addUser(email: String, password: String): Boolean {
        // Comprobamos si el email ya existe en la base de datos
        val userFind = repository.readUserByEmail(email)

        // En caso de no existir, procedemos a registrar al usuario y devolvemos true
        if(userFind == null) {
            val hashPassword = bcryptHash(password)
            repository.addUser(email, hashPassword)
            register = true
        } else { // Si existe un usuario con dicho email, devolvemos false
            register = false
        }
        return register
    }

    /* Función que comprueba si un usuario está ya registrado en la base de datos o no
     * Devuelve 0 si el email existe y la contraseña es correcta
     * Devuelve 1 si el email existe y la contraseña no es correcta
     * Devuelve 2 si el email no existe en la base de datos */
    suspend fun loginUser(email: String, password: String): Int {
        // Comprobamos si existe el email en la base de datos
        val userFind = repository.readUserByEmail(email)

        val res: Int
        // Si existe un usuario con dicho email y comprobamos si la contraseña es correcta
        if(userFind != null) {
            // Si la contraseña es correcta
            if(verifyHash(password, userFind.masterPass)) {
                res = 0
            } else {
                // Si la contraseña no es correcta
                res = 1
            }
        } else {
            // Si no existe el usuario con dicho email
            res = 2
        }

        return res
    }

    // Función que devuelve el usuario con dicho email
    suspend fun findUserByEmail(email: String): User? {
        return repository.readUserByEmail(email)
    }
}