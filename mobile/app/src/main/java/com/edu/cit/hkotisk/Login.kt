package com.edu.cit.hkotisk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edu.cit.hkotisk.data.api.RetrofitClient
import com.edu.cit.hkotisk.data.model.AuthRequest
import com.edu.cit.hkotisk.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        // Password visibility toggle
        binding.passwordField.setOnTouchListener { _, event ->
            if (event.rawX >= binding.passwordField.right - binding.passwordField.compoundDrawables[2].bounds.width()) {
                togglePasswordVisibility()
                true
            } else false
        }

        // Login button click
        binding.loginButton.setOnClickListener {
            if (validateForm()) {
                performLogin()
            }
        }

        // Sign up click
        binding.signUpLink.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        binding.passwordField.apply {
            inputType = if (isPasswordVisible) {
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_lock,
                    0,
                    R.drawable.ic_eye_off,
                    0
                )
                android.text.InputType.TYPE_CLASS_TEXT
            } else {
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_lock,
                    0,
                    R.drawable.ic_eye,
                    0
                )
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            setSelection(length())
        }
    }

    private fun validateForm(): Boolean {
        val email = binding.usernameField.text.toString().trim()
        val password = binding.passwordField.text.toString()

        if (email.isEmpty()) {
            binding.usernameField.error = "Email is required"
            return false
        }
        if (password.isEmpty()) {
            binding.passwordField.error = "Password is required"
            return false
        }
        return true
    }

    private fun performLogin() {
        val email = binding.usernameField.text.toString().trim()
        val password = binding.passwordField.text.toString()

        binding.loginButton.isEnabled = false
        
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.createAuthService(this@Login).signIn(AuthRequest.SignInRequest(email, password))
                
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    // Save token and role
                    getSharedPreferences("auth_prefs", MODE_PRIVATE).edit().apply {
                        putString("token", authResponse.token)
                        putString("role", authResponse.role)
                        apply()
                    }

                    // Navigate to Dashboard
                    startActivity(Intent(this@Login, Dashboard::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                } else {
                    Toast.makeText(
                        this@Login,
                        "Login failed: Invalid credentials",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@Login,
                    "Login failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.loginButton.isEnabled = true
            }
        }
    }
}
