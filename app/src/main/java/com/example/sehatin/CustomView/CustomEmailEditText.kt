package com.example.sehatin.CustomView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText

class CustomEmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {

    var isEmailValid = false

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()

                if (email.isEmpty()) {
                    isEmailValid = false
                    error = null
                    removeValidIcon()
                } else if (!email.endsWith("@gmail.com", ignoreCase = true)) {
                    isEmailValid = false
                    // [PERBAIKAN] Gunakan setError(pesan, null) agar ikon (!) tidak muncul
                    setError("Email harus menggunakan @gmail.com", null)
                    removeValidIcon() // Pastikan ikon hijau hilang jika format kembali salah
                } else {
                    isEmailValid = true
                    error = null
                    showValidIcon()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showValidIcon() {
        val startDrawable = compoundDrawablesRelative[0]
        val endIcon = ContextCompat.getDrawable(context, android.R.drawable.presence_online)
        compoundDrawablePadding = 16
        setCompoundDrawablesRelativeWithIntrinsicBounds(startDrawable, null, endIcon, null)
    }

    private fun removeValidIcon() {
        val startDrawable = compoundDrawablesRelative[0]
        setCompoundDrawablesRelativeWithIntrinsicBounds(startDrawable, null, null, null)
    }
}