package com.uc3m.whatthepass.passwordApi.api

import com.uc3m.whatthepass.passwordApi.util.constants.Companion.PASS_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

  private val retrofitPass by lazy {
    Retrofit.Builder()
      .baseUrl(PASS_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  val passAPI: PasswordApi by lazy {
    retrofitPass.create(PasswordApi::class.java)
  }
}