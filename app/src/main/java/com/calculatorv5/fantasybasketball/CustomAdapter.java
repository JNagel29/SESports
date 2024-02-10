package com.calculatorv5.fantasybasketball;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder> {
    private Context context;
    //Now, we declare a list of Game objects
    private List<Game> list;
    //Constructor for the adapter
    public CustomAdapter(Context context, List<Game> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.single_games, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        //Here we actually 'set' the text for each game, position is built in and tracks curr game
        holder.textHome.setText(list.get(position).getHomeName());
        holder.textAway.setText(list.get(position).getAwayName());
        holder.textHomeScore.setText(list.get(position).getHomeScore());
        holder.textAwayScore.setText(list.get(position).getAwayScore());
        holder.textGameStatus.setText(list.get(position).getGameStatus());
    }

    @Override
    public int getItemCount() {
        return list.size(); // Returns number of games if ever needed
    } // Pass in View Holder
}
