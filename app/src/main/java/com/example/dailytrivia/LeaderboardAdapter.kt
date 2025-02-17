package com.example.dailytrivia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LeaderboardAdapter(private val players: List<Player>) :
    RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard_player, parent, false)
        return LeaderboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val player = players[position]
        holder.friendImage.setImageResource(player.imageResId) // Set circular image
        holder.bind(player)
    }

    override fun getItemCount() = players.size

    class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPlayerName: TextView = itemView.findViewById(R.id.playerName)
        private val tvPlayerScore: TextView = itemView.findViewById(R.id.playerScore)
        private val tvPlayerPosition: TextView = itemView.findViewById(R.id.playerPosition)
        val friendImage: ImageView = itemView.findViewById(R.id.friendImage)

        fun bind(player: Player) {
            tvPlayerName.text = player.name
            tvPlayerScore.text = "${player.score} Streaks"
            tvPlayerPosition.text = player.position
        }
    }
}
