package com.uc3m.whatthepass.passwordApi.repository

import com.uc3m.whatthepass.passwordApi.api.RetrofitInstance
import com.uc3m.whatthepass.passwordApi.model.PassSearched
import retrofit2.Response

class Repository {

    suspend fun getPassInfo(hash: String): Response<PassSearched> {
        return RetrofitInstance.passAPI.getPassBreach(hash)
    }
}
