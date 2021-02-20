package com.uc3m.whatthepass.models

import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity(tableName = "Password",
        foreignKeys = [ForeignKey(entity = User::class, parentColumns = ["email"], childColumns = ["user"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)],
        indices = [Index(value = ["name"], unique = true)])
data class Password(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val name: String,
        val user: User,
        val hashPassword: Blob,
        val url: String? = null
)
