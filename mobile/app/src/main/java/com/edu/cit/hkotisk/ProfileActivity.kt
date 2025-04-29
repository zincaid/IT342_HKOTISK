package com.edu.cit.hkotisk

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_profile

        // Initialize profile views
        val nameTextView = findViewById<TextView>(R.id.name_value)
        val emailTextView = findViewById<TextView>(R.id.email_value)
        val editProfileButton = findViewById<Button>(R.id.edit_profile_button)
        val logoutButton = findViewById<Button>(R.id.logout_button)

        // TODO: Load user data from preferences or API
        nameTextView.text = "John Doe"
        emailTextView.text = "john.doe@example.com"

        editProfileButton.setOnClickListener {
            // TODO: Implement edit profile functionality
        }

        logoutButton.setOnClickListener {
            // TODO: Implement logout functionality
            finish()
        }
        
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    finish()
                    true
                }
                R.id.navigation_orders -> {
                    startActivity(android.content.Intent(this, OrdersActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_cart -> {
                    startActivity(android.content.Intent(this, CartActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
