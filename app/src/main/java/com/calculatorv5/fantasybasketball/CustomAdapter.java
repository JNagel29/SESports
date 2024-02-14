package com.calculatorv5.fantasybasketball;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    //Now, we declare a list of objects
    private final List<?> items; // ? is wildcard, we need it cause we don't know if we'll get game/player
    //Constants to differentiate between game/player type
    private static final int VIEW_TYPE_GAME = 1;
    private static final int VIEW_TYPE_PLAYER = 2;
    //Listener for Players
    private final SelectListener listener;
    //Constructor for the adapter
    public CustomAdapter(Context context, List<?> items, SelectListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Game) return VIEW_TYPE_GAME;
        else if (items.get(position) instanceof Player) return VIEW_TYPE_PLAYER;
        else return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_GAME) return new GameViewHolder(LayoutInflater.from(context).inflate(R.layout.single_game, parent, false));
        else if (viewType == VIEW_TYPE_PLAYER) return new PlayerViewHolder(LayoutInflater.from(context).inflate(R.layout.single_player, parent, false));
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_GAME:
                GameViewHolder gameViewHolder = (GameViewHolder) holder; // Grabs game holder
                Game game = (Game) items.get(position); // Grabs current game object
                // Binds everything using the current game object's getters
                gameViewHolder.textHome.setText(game.getHomeName());
                gameViewHolder.textAway.setText(game.getAwayName());
                gameViewHolder.textHomeScore.setText(game.getHomeScore());
                gameViewHolder.textAwayScore.setText(game.getAwayScore());
                gameViewHolder.textGameStatus.setText(game.getGameStatus());
                gameViewHolder.textGameTime.setText(game.getGameTime());
                gameViewHolder.imageHomeLogo.setImageResource(game.getHomeLogo());
                gameViewHolder.imageAwayLogo.setImageResource(game.getAwayLogo());
                break;
            case VIEW_TYPE_PLAYER:
                PlayerViewHolder playerViewHolder = (PlayerViewHolder) holder; // Grabs game holder
                Player player = (Player) items.get(position); // Grabs current player object
                // Binds using current player's objects
                playerViewHolder.playerName.setText(player.getPlayerName());
                //Sets up our card listener to open a profile
                playerViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       listener.onItemClicked(player);
                   }
                });
                break;
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    } // Pass in View Holder
}
