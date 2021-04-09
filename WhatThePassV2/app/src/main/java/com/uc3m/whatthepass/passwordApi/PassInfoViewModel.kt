package com.uc3m.whatthepass.passwordApi

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uc3m.whatthepass.passwordApi.model.PassSearched
import com.uc3m.whatthepass.passwordApi.repository.Repository
import retrofit2.Response

class PassInfoViewModel(private val repository: Repository) : ViewModel() {

  val myPasswordResponse: MutableLiveData<Response<PassSearched>> = MutableLiveData()

  suspend fun getPasswordInfo(hash: String){
    val response = repository.getPassInfo(hash)
    myPasswordResponse.value = response
  }

}