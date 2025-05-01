package com.edu.cit.hkotisk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LandingPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if token exists in SharedPreferences
        val sharedPrefs = getSharedPreferences("secure_prefs", MODE_PRIVATE)
        val token = sharedPrefs.getString("auth_token", null)
        
        if (token != null) {
            // Token exists, redirect to Dashboard
            startActivity(Intent(this, Dashboard::class.java))
            finish()
        } else {
            // No token, show landing page
            setContentView(R.layout.activity_landing_page)
        }
    }
}
