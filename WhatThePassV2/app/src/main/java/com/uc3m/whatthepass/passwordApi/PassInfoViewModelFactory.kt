package com.uc3m.whatthepass.passwordApi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uc3m.whatthepass.passwordApi.repository.Repository

@Suppress("UNCHECKED_CAST")
class PassInfoViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PassInfoViewModel(repository) as T
    }
}
