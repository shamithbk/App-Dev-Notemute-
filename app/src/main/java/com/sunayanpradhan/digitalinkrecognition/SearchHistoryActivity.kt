package com.sunayanpradhan.digitalinkrecognition

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchHistoryActivity : AppCompatActivity(){

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_history)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Retrieve search history from StrokeManager
        val searchHistory = StrokeManager.getSearchHistory()

        // Convert the list of strings to a list of SearchHistoryItem objects
        val historyItems = searchHistory.map { SearchHistoryItem(it, System.currentTimeMillis()) }

        // Initialize the adapter with the historyItems list
        adapter = SearchHistoryAdapter(historyItems.toMutableList())
        recyclerView.adapter = adapter
    }

}
