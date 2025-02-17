package com.example.dailytrivia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LeaderboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        // RecyclerView for the full leaderboard list
        val rvLeaderboard = findViewById<RecyclerView>(R.id.rvLeaderboard)
        rvLeaderboard.layoutManager = LinearLayoutManager(this)
        // Initialize and set the adapter for leaderboard data
        rvLeaderboard.adapter = LeaderboardAdapter(getLeaderboardData())
    }

    // Sample function to get leaderboard data (replace with real data)
    private fun getLeaderboardData(): List<Player> {
        return listOf(
            Player("Alice", 880, "1st Place", R.drawable.p1),
            Player("Bob", 860, "2nd Place", R.drawable.p2),
            Player("Charlie", 840, "3rd Place", R.drawable.p3),
            Player("Daisy", 820, "4th Place", R.drawable.p4),
            Player("Edward", 800, "5th Place", R.drawable.p5)
            // Add more players as needed
        )
    }
}
