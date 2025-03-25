package com.edu.cit.hkotisk

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edu.cit.hkotisk.data.api.RetrofitClient
import com.edu.cit.hkotisk.data.model.SignUpRequest
import com.edu.cit.hkotisk.databinding.ActivitySignupBinding
import kotlinx.coroutines.launch

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        // Password visibility toggles
        binding.passwordLayout.setEndIconOnClickListener {
            togglePasswordVisibility()
        }

        binding.confirmPasswordLayout.setEndIconOnClickListener {
            toggleConfirmPasswordVisibility()
        }

        // Create account button click
        binding.createAccountButton.setOnClickListener {
            if (validateForm()) {
                performSignUp()
            }
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        binding.passwordLayout.apply {
            endIconDrawable = resources.getDrawable(
                if (isPasswordVisible) R.drawable.ic_eye_off else R.drawable.ic_eye,
                theme
            )
            editText?.inputType = if (isPasswordVisible) {
                android.text.InputType.TYPE_CLASS_TEXT
            } else {
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            editText?.setSelection(editText?.length() ?: 0)
        }
    }

    private fun toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible
        binding.confirmPasswordLayout.apply {
            endIconDrawable = resources.getDrawable(
                if (isConfirmPasswordVisible) R.drawable.ic_eye_off else R.drawable.ic_eye,
                theme
            )
            editText?.inputType = if (isConfirmPasswordVisible) {
                android.text.InputType.TYPE_CLASS_TEXT
            } else {
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            editText?.setSelection(editText?.length() ?: 0)
        }
    }

    private fun validateForm(): Boolean {
        val username = binding.usernameLayout.editText?.text.toString().trim()
        val password = binding.passwordLayout.editText?.text.toString()
        val confirmPassword = binding.confirmPasswordLayout.editText?.text.toString()

        if (username.isEmpty()) {
            binding.usernameLayout.error = "Username or Email is required"
            return false
        }
        if (password.isEmpty()) {
            binding.passwordLayout.error = "Password is required"
            return false
        }
        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordLayout.error = "Please confirm your password"
            return false
        }
        if (password != confirmPassword) {
            binding.confirmPasswordLayout.error = "Passwords do not match"
            return false
        }

        // Clear any previous errors
        binding.usernameLayout.error = null
        binding.passwordLayout.error = null
        binding.confirmPasswordLayout.error = null

        return true
    }

    private fun performSignUp() {
        val username = binding.usernameLayout.editText?.text.toString().trim()
        val password = binding.passwordLayout.editText?.text.toString()

        binding.createAccountButton.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.authService.signUp(
                    SignUpRequest(
                        email = username,
                        username = username,
                        password = password
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(
                        this@SignUp,
                        "Account created successfully! Please sign in.",
                        Toast.LENGTH_LONG
                    ).show()
                    
                    // Navigate to login screen
                    startActivity(Intent(this@SignUp, Login::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@SignUp,
                        "Sign up failed: ${response.errorBody()?.string() ?: "Unknown error"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@SignUp,
                    "Sign up failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.createAccountButton.isEnabled = true
            }
        }
    }
}
