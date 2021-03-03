package com.uc3m.whatthepass.models

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "Password",
        //foreignKeys = [ForeignKey(entity = User::class, parentColumns = ["email"], childColumns = ["user"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)],
        indices = [Index(value = ["name"], unique = true)])
data class Password(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val name: String,
        @ForeignKey
                (entity = User::class,
                parentColumns = ["email"],
                childColumns = ["user"],
                onDelete = CASCADE,
                onUpdate = CASCADE
                )
        val user: String,
       // @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val hashPassword: String,
        val url: String?
) {



}
