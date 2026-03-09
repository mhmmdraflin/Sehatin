package com.example.sehatin.CustomView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText

class CustomConfirmPasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {

    var isConfirmValid = false
    var targetPasswordView: TextInputEditText? = null

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val confirmPass = s.toString()
                val targetPass = targetPasswordView?.text?.toString() ?: ""

                if (confirmPass.isEmpty()) {
                    isConfirmValid = false
                    error = null
                    removeValidIcon()
                } else if (confirmPass != targetPass) {
                    isConfirmValid = false
                    // [PERBAIKAN] Gunakan setError(pesan, null)
                    setError("Konfirmasi password tidak sesuai", null)
                    removeValidIcon()
                } else {
                    isConfirmValid = true
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