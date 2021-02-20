package com.uc3m.whatthepass.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "File", foreignKeys = [ForeignKey(entity = User::class, parentColumns = ["email"], childColumns = ["user"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)])
data class File(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val name: String,
        val path: String,
        val user: User
)
