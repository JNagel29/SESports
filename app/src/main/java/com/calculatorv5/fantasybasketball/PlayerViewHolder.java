package com.calculatorv5.fantasybasketball;

import android.widget.TextView;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class PlayerViewHolder extends RecyclerView.ViewHolder {
    public TextView playerName;
    public CardView cardView;

    public PlayerViewHolder(@NonNull View itemView) {
        super(itemView); // Calls parent View Holder to set up
        //Now, we connect the player name
        playerName = itemView.findViewById(R.id.textPlayerName);
        cardView = itemView.findViewById(R.id.main_container);
    }
}
