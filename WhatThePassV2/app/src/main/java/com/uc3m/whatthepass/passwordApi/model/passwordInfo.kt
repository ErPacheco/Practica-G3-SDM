package com.uc3m.whatthepass.passwordApi.model

import com.google.gson.annotations.SerializedName

// Modelo devuelto por la API que contiene:
data class PasswordInfo(
    var anon: String,
    var char: String,
    var count: String,
    var wordList: Int
)

data class PassSearched(
    @SerializedName("SearchPassAnon")
    var passData: PasswordInfo
)
