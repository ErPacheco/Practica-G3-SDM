package com.uc3m.whatthepass.models

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {

    val readAll: LiveData<List<User>> = userDao.readAll()

    suspend fun readUserByEmail(email: String): User? {
        return userDao.findUserByEmail(email)
    }

    suspend fun addUser(email: String, password: String) {
        val user = User(email = email, masterPass = password)
        userDao.addUser(user)
    }

}
