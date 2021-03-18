package com.uc3m.whatthepass.models

import androidx.lifecycle.LiveData

class FileRepository (private val fileDao: FileDao) {

    val readAll: LiveData<List<File>> = fileDao.readAll()

    suspend fun addFile(name: String, path: String, user:String) {
        val file = File(0, name, path, user)
        fileDao.addFile(file)
    }

    suspend fun findFileByUser (user: String){
        fileDao.findByUser(user)
    }

    suspend fun deleteFileByUser (user:String){
        fileDao.deleteByUser(user)
    }

    suspend fun deleteFile (file:File){
        fileDao.deleteFile(file)
    }

    suspend fun updateFile (name: String, path: String, user:String) {
        val file = File(0, name, path, user)
        fileDao.updateFile(file)
    }
}