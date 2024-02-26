package com.calculatorv5.fantasybasketball;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomViewHolder extends RecyclerView.ViewHolder{
    public TextView textHome, textAway, textHomeScore, textAwayScore, textGameStatus;
    public ImageView imageHomeLogo, imageAwayLogo;
    public CustomViewHolder(@NonNull View itemView) {
        super(itemView); // This calls the parent View Holder to 'set it up'
        //Connect textview and imageview to the layout ID's
        textHome = itemView.findViewById(R.id.textHome);
        textAway = itemView.findViewById(R.id.textAway);
        textHomeScore = itemView.findViewById(R.id.textHomeScore);
        textAwayScore = itemView.findViewById(R.id.textAwayScore);
        textGameStatus = itemView.findViewById(R.id.textGameStatus);
        imageHomeLogo = itemView.findViewById(R.id.imageHomeLogo);
        imageAwayLogo = itemView.findViewById(R.id.imageAwayLogo);
    }
}
