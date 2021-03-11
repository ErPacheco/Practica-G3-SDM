package com.uc3m.whatthepass.models

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {

    val readAll: LiveData<List<User>> = userDao.readAll()

    suspend fun readUser(email: String, password: String): User? {
        return userDao.findUserByEmailAndMasterPass(email, password)
    }

    suspend fun readUserEmail(email: String): User {
        return userDao.findUserByEmail(email)
    }

    suspend fun addUser(email: String, password: String) {
        val user = User(email = email, masterPass = password)
        userDao.addUser(user)
    }

    /*suspend fun updateUser(email: String, password: Blob) {
        val user = User(email = email, masterPass = password)
        userDao.updateUser(user)
    }*/

    suspend fun deleteUser(email: String) {
        val user = userDao.findUserByEmail(email)
        userDao.deleteUser(user)
    }
}