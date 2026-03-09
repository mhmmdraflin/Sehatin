package com.example.sehatin.Auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterViewModel (private val repository: RegisterRepository) : ViewModel() {

    // LiveData untuk memantau status loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData untuk memberikan hasil registrasi ke Activity
    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> = _registerResult

    fun register(nama: String, email: String, pass: String) {
        viewModelScope.launch {
            // 1. Tampilkan Loading
            _isLoading.value = true

            // 2. Jeda Buatan (Simulasi proses ke database)
            delay(1500) // Tahan layar loading selama 1.5 detik

            // 3. Proses penyimpanan via Repository
            repository.registerUser(nama, email, pass)

            // 4. Sembunyikan Loading
            _isLoading.value = false

            // 5. Kirimkan hasil sukses (karena ini SharedPreferences, kita anggap selalu sukses)
            _registerResult.value = true
        }
    }
}