package com.example.jetpacktest.props

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jetpacktest.R

class PlayerAdapter(
    private val players: List<Player>,
    private val onPlayerClick: (Player) -> Unit  // Lambda to handle item clicks
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.nameText)
        val teamText: TextView = itemView.findViewById(R.id.teamText)
        val infoText: TextView = itemView.findViewById(R.id.infoText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        // Inflate the item_player layout (make sure item_player.xml exists under res/layout)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]
        holder.nameText.text = player.name
        holder.teamText.text = "${player.team} - ${player.position} #${player.jerseyNumber}"
        holder.infoText.text = "Next: ${player.nextOpponent} at ${player.nextGameTime}"

        // Set the click listener to trigger the callback with the current player
        holder.itemView.setOnClickListener {
            onPlayerClick(player)
        }
    }

    override fun getItemCount(): Int = players.size
}
