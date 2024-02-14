package com.calculatorv5.fantasybasketball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;



public class SearchActivity extends AppCompatActivity implements SelectListener {
    TextView data;
    SearchView searchView;
    //These three will be used to list out the player profiles via a scrollable recyclerView
    RecyclerView recyclerView;
    List<Player> myPlayers = new ArrayList<>();
    CustomAdapter customAdapter;
    String basePlayerUrl = "https://nba-stats-db.herokuapp.com/api/playerdata/name/";
    // This is a flag for whether or not to finish (aka terminate) activities while switching
    // If we don't finish, then the back button can be used to go to previous page
    private final boolean shouldFinish = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Declares and calls function to set up Bottom Navigation Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        setupBottomNavigation(bottomNavigationView);

        searchView = findViewById(R.id.searchView);
        searchView.clearFocus(); //Older version's auto open keyboard, this removes that

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                myPlayers.clear(); // Clears out any past results before handling new query
                getPlayerDataV2(query);
                displayItems();
                searchView.clearFocus(); // dismisses keyboard after search
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void displayItems() {
        recyclerView = findViewById(R.id.recycler_players);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        //Then, we declare the adapter and set it to the recycler view
        customAdapter = new CustomAdapter(this, myPlayers, this);
        recyclerView.setAdapter(customAdapter);
    }


    public void setupBottomNavigation(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setSelectedItemId(R.id.bottom_person_search);
        //Note: had to upgrade gradle material to 1.11.0 for setOnItemSelectedListener to work
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                // This line basically says to perform the transition right after activity start
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //This line 'terminates' the current activity since we're switching
                if (shouldFinish) finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_person_search) {
                return true;
            } else if (item.getItemId() == R.id.bottom_compare) {
                startActivity(new Intent(getApplicationContext(), CompareActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                if (shouldFinish) finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_games) {
                startActivity(new Intent(getApplicationContext(), GamesActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                if (shouldFinish) finish();
                return true;
            }
            return false;
        });
    }

    public void getPlayerData(String playerName) {
        String playerURL = basePlayerUrl + playerName;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, playerURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray resultsArr = response.getJSONArray("results"); // Grabs results of all years
                    JSONObject firstResult = resultsArr.getJSONObject(0); // Grabs most recent year
                    String playerName = firstResult.getString("player_name");
                    int age = firstResult.getInt("age");
                    String fgPercent = firstResult.getString("field_percent");
                    String threePercent = firstResult.getString("three_percent");
                    String team = firstResult.getString("team");
                    String season = firstResult.getString("season");

                    String playerData = "Player Name: " + playerName + "\nCurrent Team: " + team + "\nAge: " + age +
                            "\nField Goal Percentage: " + fgPercent + "\nThree Point Percentage: " + threePercent +
                            "\nSeason: " + season + "\n";
                    data.setText(playerData);
                } catch (JSONException e) {
                    e.printStackTrace();
                    data.setText("It's possible that " + playerName + " wasn't a valid player!");
                    //data.setText(playerURL);
                    //data.setText(R.string.error);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                data.setText("It's possible that " + playerName + " wasn't a valid player!");
            }
        });
        Volley.newRequestQueue(this).add(request);
    }

    public void getPlayerDataV2(String playerName) {
        String playerURL = basePlayerUrl + playerName;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, playerURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray arrPlayers = response.getJSONArray("results"); // Grabs each player from each szn
                    JSONObject playerInstance;
                    String playerName;
                    HashSet<String> playerSet = new HashSet<String>();
                    for (int i=0; i<arrPlayers.length(); i++) {
                        playerInstance = arrPlayers.getJSONObject(i);
                        //playerSet.add(playerInstance.getString("player_name"));
                        playerName = playerInstance.getString("player_name");
                        boolean playerFound = false;
                        for (int j=0; j < myPlayers.size(); j++) {
                            if ( (myPlayers.get(j).getPlayerName()).equals(playerName))
                                playerFound = true;
                        }
                        if (!playerFound) myPlayers.add(new Player(playerName));
                    }
                    // Here, we update view with most recent data, very important since its async
                    customAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    data.setText("It's possible that " + playerName + " wasn't a valid player!");
                    //data.setText(playerURL);
                    //data.setText(R.string.error);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                data.setText("It's possible that " + playerName + " wasn't a valid player!");
            }
        });
        Volley.newRequestQueue(this).add(request);
    }

    //Small Override that makes it so back button changes selected icon
    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_person_search);
    }
    //Click listener for each player card that passes name to profile activity to use
    @Override
    public void onItemClicked(Player player) {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.putExtra("playerName", player.getPlayerName());
        intent.setClass(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }
}