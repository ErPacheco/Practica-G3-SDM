package com.uc3m.whatthepass.passwordApi

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uc3m.whatthepass.passwordApi.model.PassSearched
import com.uc3m.whatthepass.passwordApi.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class PassInfoViewModel(private val repository: Repository) : ViewModel() {

  val myPasswordResponse: MutableLiveData<Response<PassSearched>> = MutableLiveData()

  fun getPasswordInfo(hash: String){
    viewModelScope.launch {
      val response = repository.getPassInfo(hash)
      Log.d("RESPONSE ------> ", response.toString())
      myPasswordResponse.value = response
    }
  }

}