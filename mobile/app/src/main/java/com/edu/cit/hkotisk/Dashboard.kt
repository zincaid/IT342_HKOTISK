package com.edu.cit.hkotisk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.edu.cit.hkotisk.databinding.ActivityDashboardBinding

class Dashboard : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
