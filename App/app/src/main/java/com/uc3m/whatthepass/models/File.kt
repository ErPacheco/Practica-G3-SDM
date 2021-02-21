package com.uc3m.whatthepass.models

import androidx.room.*

@Entity(tableName = "File")
data class File(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val name: String,
        val path: String,
        @ForeignKey
                (entity = User::class,
                parentColumns = ["email"],
                childColumns = ["user"],
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
        val user: String,




)
