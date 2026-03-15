package com.example.sehatin.Auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: LoginRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1500) // Simulasi loading

            val isSuccess = repository.checkLogin(email, pass)
            if (isSuccess) {
                repository.setLoginSuccess(email) // Kunci identitas aktif
            }

            _isLoading.value = false
            _loginResult.value = isSuccess
        }
    }

    fun isRememberMe(): Boolean = repository.isRememberMe()
    fun getSavedEmail(): String? = repository.getSavedEmail()
    fun getSavedPassword(): String? = repository.getSavedPassword()
    fun saveRememberMeStatus(isChecked: Boolean) = repository.setRememberMe(isChecked)
}