package com.uc3m.whatthepass.models

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.sql.Blob

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
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val hashPassword: ByteArray,
        val url: String?
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Password

                if (id != other.id) return false
                if (name != other.name) return false
                if (user != other.user) return false
                if (!hashPassword.contentEquals(other.hashPassword)) return false
                if (url != other.url) return false

                return true
        }

        override fun hashCode(): Int {
                var result = id
                result = 31 * result + name.hashCode()
                result = 31 * result + user.hashCode()
                result = 31 * result + hashPassword.contentHashCode()
                result = 31 * result + (url?.hashCode() ?: 0)
                return result
        }
}
