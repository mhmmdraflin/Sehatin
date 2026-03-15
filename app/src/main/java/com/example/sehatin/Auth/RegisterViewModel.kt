package com.example.sehatin.Auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: RegisterRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> = _registerResult

    fun register(nama: String, email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1500)

            repository.registerUser(nama, email, pass)

            _isLoading.value = false
            _registerResult.value = true
        }
    }
}