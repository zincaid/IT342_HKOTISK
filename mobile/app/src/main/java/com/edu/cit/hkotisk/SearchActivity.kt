package com.edu.cit.hkotisk

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.cit.hkotisk.adapter.RecentSearchAdapter
import com.edu.cit.hkotisk.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var recentSearchAdapter: RecentSearchAdapter
    private val recentSearches = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupRecentSearches()
    }

    private fun setupViews() {
        // Setup cancel button
        binding.cancelButton.setOnClickListener {
            finish()
        }

        // Setup clear all button
        binding.clearAllButton.setOnClickListener {
            recentSearches.clear()
            recentSearchAdapter.notifyDataSetChanged()
            // In a real app, you would also clear from persistent storage
        }

        // Setup search action
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchQuery = binding.searchEditText.text.toString()
                if (searchQuery.isNotEmpty()) {
                    addToRecentSearches(searchQuery)
                    // Perform search (implement your search logic here)
                    return@setOnEditorActionListener true
                }
            }
            false
        }
    }

    private fun setupRecentSearches() {
        // Initialize with sample data (in a real app, load from persistent storage)
        recentSearches.addAll(listOf(
            "Subway",
            "Burgers",
            "Sandwich",
            "Pizza",
            "Fried Rice with meat",
            "Bakery",
            "Cake",
            "Cookies"
        ))

        // Setup RecyclerView
        recentSearchAdapter = RecentSearchAdapter(recentSearches) { query ->
            binding.searchEditText.setText(query)
            // Perform search with the selected query
        }

        binding.recentSearchesRecycler.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = recentSearchAdapter
        }
    }

    private fun addToRecentSearches(query: String) {
        // Remove if already exists (to move it to top)
        recentSearches.remove(query)
        // Add to beginning of list
        recentSearches.add(0, query)
        recentSearchAdapter.notifyDataSetChanged()
        // In a real app, you would also save to persistent storage
    }
} 