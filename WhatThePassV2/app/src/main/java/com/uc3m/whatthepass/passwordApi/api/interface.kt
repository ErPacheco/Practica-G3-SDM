package com.uc3m.whatthepass.passwordApi.api

import com.uc3m.whatthepass.passwordApi.model.PassSearched
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PasswordApi {

  @GET("api/v1/pass/anon/{hash}")
  suspend fun getPassBreach(@Path("hash") hash: String): Response<PassSearched>
}