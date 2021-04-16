package com.uc3m.whatthepass.passwordApi.api

import com.uc3m.whatthepass.passwordApi.util.constants.Companion.PASS_URL
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

  private val retrofitPass by lazy {
    val certificatePinner = CertificatePinner.Builder()
      .add("passwords.xposedornot.com", "sha256/PcIiExMyjUj9Dt9n0LFGwIzVwwoxcMryiWILZhUZ6as=").build()

    val okHttpClient = OkHttpClient.Builder().certificatePinner(certificatePinner).build()

    Retrofit.Builder()
      .baseUrl(PASS_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .client(okHttpClient)
      .build()
  }

  val passAPI: PasswordApi by lazy {
    retrofitPass.create(PasswordApi::class.java)
  }
}