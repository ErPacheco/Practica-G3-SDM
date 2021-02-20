package com.uc3m.whatthepass.models

import java.sql.Blob

class UserRepository(private val userDao: UserDao) {

    suspend fun readUser(email: String, password: String) {
        userDao.findUserByEmailAndMasterPass(email, password)
    }

    suspend fun addUser(email: String, password: Blob) {
        val user = User(email = email, masterPass = password)
        userDao.addUser(user)
    }

    /*suspend fun updateUser(email: String, password: Blob) {
        val user = User(email = email, masterPass = password)
        userDao.updateUser(user)
    }*/

    suspend fun deleteUser(email: String) {
        val user = userDao.findUserByEmail(email)
        if(user != null) {
            userDao.deleteUser(user)
        }
    }
}