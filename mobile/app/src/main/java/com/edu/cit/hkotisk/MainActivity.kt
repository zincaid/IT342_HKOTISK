package com.edu.cit.hkotisk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starter_page)

        Handler(Looper.getMainLooper()).postDelayed({
            // Check if token exists in SharedPreferences
            val sharedPrefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
            val token = sharedPrefs.getString("token", null)
            
            if (token != null) {
                // Token exists, redirect to Dashboard
                startActivity(Intent(this, Dashboard::class.java))
            } else {
                // No token, go to Login
                startActivity(Intent(this, Login::class.java))
            }
            finish()
        }, 2000)
    }
}
